package com.github.malow.accountserver.database;

import java.util.concurrent.ConcurrentHashMap;

import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.database.Accessor;
import com.github.malow.malowlib.database.DatabaseConnection;
import com.github.malow.malowlib.database.DatabaseExceptions.ForeignKeyException;
import com.github.malow.malowlib.database.DatabaseExceptions.MissingMandatoryFieldException;
import com.github.malow.malowlib.database.DatabaseExceptions.MultipleRowsReturnedException;
import com.github.malow.malowlib.database.DatabaseExceptions.SimultaneousModificationException;
import com.github.malow.malowlib.database.DatabaseExceptions.UnexpectedException;
import com.github.malow.malowlib.database.DatabaseExceptions.UniqueException;
import com.github.malow.malowlib.database.DatabaseExceptions.ZeroRowsReturnedException;
import com.github.malow.malowlib.database.PreparedStatementPool;

public class AccountAccessor extends Accessor<Account>
{
  public static class WrongAuthentificationTokenException extends Exception
  {
    private static final long serialVersionUID = 1L;
  }

  private ConcurrentHashMap<String, Account> cacheByEmail = new ConcurrentHashMap<>();

  private PreparedStatementPool readByEmailStatements;
  private PreparedStatementPool updatePwResetTokenStatements;

  public AccountAccessor(DatabaseConnection databaseConnection)
  {
    super(databaseConnection);
    this.readByEmailStatements = this.createPreparedStatementPool("SELECT * FROM " + this.tableName + " WHERE email = ?");
    this.updatePwResetTokenStatements = this.createPreparedStatementPool("UPDATE " + this.tableName + " SET pwResetToken = ? WHERE email = ?");
  }

  @Override
  public Account create(Account account) throws UniqueException, ForeignKeyException, MissingMandatoryFieldException, UnexpectedException
  {
    account = super.create(account);
    this.cacheByEmail.put(account.email, account);
    return account;
  }

  @Override
  public Account read(Integer id) throws ZeroRowsReturnedException, MultipleRowsReturnedException, UnexpectedException
  {
    Account account = super.read(id);
    this.cacheByEmail.put(account.email, account);
    return account;
  }

  @Override
  public void update(Account account) throws SimultaneousModificationException, MultipleRowsReturnedException, UnexpectedException
  {
    super.update(account);
    this.cacheByEmail.put(account.email, account);
  }

  @Override
  public void delete(Integer id) throws ZeroRowsReturnedException, MultipleRowsReturnedException, UnexpectedException, ForeignKeyException
  {
    Account account = super.read(id);
    super.delete(id);
    this.cacheByEmail.remove(account);
  }

  public Account readByEmail(String email) throws ZeroRowsReturnedException, UnexpectedException
  {
    Account account = this.cacheByEmail.get(email);
    if (account != null)
    {
      return account;
    }
    try
    {
      account = this.readByEmailStatements.useStatement(statement ->
      {
        statement.setString(1, email);
        return this.readWithPopulatedStatement(statement);
      });
      this.cacheByEmail.put(account.email, account);
      return account;
    }
    catch (ZeroRowsReturnedException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw this.logAndCreateUnexpectedException(
          "Unexpected error when trying to read a " + this.entityClass.getSimpleName() + " with email " + email + " in accessor", e);
    }
  }

  public void updatePasswordResetToken(String email, String pwResetToken) throws ZeroRowsReturnedException, UnexpectedException
  {
    try
    {
      this.updatePwResetTokenStatements.useStatement(statement ->
      {
        statement.setString(1, pwResetToken);
        statement.setString(2, email);
        this.executeSingleUpdate(statement);
      });
      Account cachedAccount = this.cacheByEmail.get(email);
      if (cachedAccount != null)
      {
        cachedAccount.pwResetToken = pwResetToken;
        this.cacheByEmail.put(email, cachedAccount);
      }
    }
    catch (ZeroRowsReturnedException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw this.logAndCreateUnexpectedException(
          "Unexpected error when trying to updatePasswordResetToken a " + this.entityClass.getSimpleName() + " with email " + email + " in accessor",
          e);
    }
  }

  public void updateCacheOnly(Account acc)
  {
    this.cacheByEmail.put(acc.email, acc);
  }

  public void clearCache()
  {
    this.cacheByEmail.clear();
  }

  public boolean checkAuthToken(String email, String authToken)
  {
    Account acc = this.cacheByEmail.get(email);
    if (acc != null && acc.authToken.equals(authToken))
    {
      return true;
    }
    return false;
  }

  public Integer checkAuthTokenAndGetAccId(String email, String authToken) throws WrongAuthentificationTokenException
  {
    Account acc = this.cacheByEmail.get(email);
    if (acc != null && acc.authToken.equals(authToken))
    {
      return acc.getId();
    }
    MaloWLogger.info("AccountAccessor authentication failed for " + email + ".");
    throw new WrongAuthentificationTokenException();
  }
}

