package com.github.malow.accountserver.database;

import java.sql.PreparedStatement;
import java.util.concurrent.ConcurrentHashMap;

import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.database.Accessor;
import com.github.malow.malowlib.database.DatabaseConnection;
import com.github.malow.malowlib.database.DatabaseExceptions.ForeignKeyException;
import com.github.malow.malowlib.database.DatabaseExceptions.MissingMandatoryFieldException;
import com.github.malow.malowlib.database.DatabaseExceptions.MultipleRowsReturnedException;
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

  private ConcurrentHashMap<String, Account> cacheByEmail = new ConcurrentHashMap<String, Account>();

  private PreparedStatementPool readByEmailStatements;
  private PreparedStatementPool updatePwResetTokenStatements;

  public AccountAccessor(DatabaseConnection databaseConnection)
  {
    super(databaseConnection, Account.class);
    this.readByEmailStatements = this.createPreparedStatementPool("SELECT * FROM " + this.tableName + " WHERE email = ?");
    this.updatePwResetTokenStatements = this.createPreparedStatementPool("UPDATE " + this.tableName + " SET pwResetToken = ? WHERE email = ?");
  }

  @Override
  public Account create(Account account) throws UniqueException, ForeignKeyException, MissingMandatoryFieldException, UnexpectedException
  {
    Account acc = super.create(account);
    this.cacheByEmail.put(acc.email, acc);
    return account;
  }

  @Override
  public boolean update(Account account) throws ZeroRowsReturnedException, MultipleRowsReturnedException, UnexpectedException
  {
    super.update(account);
    this.cacheByEmail.put(account.email, account);
    return true;
  }

  public Account read(String email) throws ZeroRowsReturnedException, UnexpectedException
  {
    Account a = this.cacheByEmail.get(email);
    if (a != null)
    {
      return a;
    }
    PreparedStatement statement = null;
    try
    {
      statement = this.readByEmailStatements.get();
      statement.setString(1, email);
      Account account = this.readWithPopulatedStatement(statement);
      this.cacheByEmail.put(account.email, account);
      this.readByEmailStatements.add(statement);
      return account;
    }
    catch (ZeroRowsReturnedException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      this.closeStatement(statement);
      this.logAndReThrowUnexpectedException(
          "Unexpected error when trying to read a " + this.entityClass.getSimpleName() + " with email " + email + " in accessor", e);
    }
    return null;
  }

  public void updatePasswordResetToken(String email, String pwResetToken) throws ZeroRowsReturnedException, UnexpectedException
  {
    PreparedStatement statement = null;
    try
    {
      statement = this.updatePwResetTokenStatements.get();
      statement.setString(1, pwResetToken);
      statement.setString(2, email);
      this.updateWithPopulatedStatement(statement);
      Account cachedAccount = this.cacheByEmail.get(email);
      if (cachedAccount != null)
      {
        cachedAccount.pwResetToken = pwResetToken;
        this.cacheByEmail.put(email, cachedAccount);
      }
      this.updatePwResetTokenStatements.add(statement);
    }
    catch (ZeroRowsReturnedException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      this.closeStatement(statement);
      this.logAndReThrowUnexpectedException(
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
    throw new WrongAuthentificationTokenException();
  }

  @Override
  public void createTable()
  {
    try
    {
      super.createTable();
    }
    catch (Exception e)
    {
      MaloWLogger.error("Unexpected error when trying to createTable for " + this.entityClass.getSimpleName() + " in accessor", e);
    }
  }
}

