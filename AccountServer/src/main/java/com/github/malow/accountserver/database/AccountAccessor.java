package com.github.malow.accountserver.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ConcurrentHashMap;

import com.github.malow.accountserver.database.Database.UnexpectedException;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class AccountAccessor
{
  // Exceptions
  public static class EmailAlreadyExistsException extends Exception
  {
    private static final long serialVersionUID = 2L;
  }

  public static class UsernameAlreadyExistsException extends Exception
  {
    private static final long serialVersionUID = 3L;
  }

  public static class WrongAuthentificationTokenException extends Exception
  {
    private static final long serialVersionUID = 4L;
  }

  public static class AccountNotFoundException extends Exception
  {
    private static final long serialVersionUID = 5L;
  }

  private static ConcurrentHashMap<String, Account> cacheByEmail = new ConcurrentHashMap<String, Account>();

  // CRUD Methods
  public static Account read(String email) throws UnexpectedException, AccountNotFoundException
  {
    Account a = cacheByEmail.get(email);
    if (a != null) return a;
    try
    {
      PreparedStatement s1 = Database.getConnection().prepareStatement("SELECT * FROM Accounts WHERE email = ? ; ");
      s1.setString(1, email);
      ResultSet s1Res = s1.executeQuery();

      if (s1Res.next())
      {
        Long id = s1Res.getLong("id");
        String username = s1Res.getString("username");
        String password = s1Res.getString("password");
        String pwResetToken = s1Res.getString("pw_reset_token");
        int failedLoginAttempts = s1Res.getInt("failed_login_attempts");

        Account acc = new Account(id, username, password, email, pwResetToken, failedLoginAttempts, null);
        s1Res.close();
        s1.close();
        cacheByEmail.put(acc.email, acc);
        return acc;
      }
    }
    catch (Exception e)
    {
      UnexpectedException ue = new UnexpectedException(e.toString());
      ue.setStackTrace(e.getStackTrace());
      throw ue;
    }
    throw new AccountNotFoundException();
  }

  public static boolean create(Account acc) throws UnexpectedException, EmailAlreadyExistsException, UsernameAlreadyExistsException
  {
    try
    {
      PreparedStatement s = Database.getConnection().prepareStatement("insert into Accounts values (default, ?, ?, ?, ?, ?);",
          Statement.RETURN_GENERATED_KEYS);
      int i = 1;
      s.setString(i++, acc.username);
      s.setString(i++, acc.password);
      s.setString(i++, acc.email);
      s.setString(i++, acc.pwResetToken);
      s.setInt(i++, acc.failedLoginAttempts);
      int rowCount = s.executeUpdate();
      ResultSet generatedKeys = s.getGeneratedKeys();
      if (rowCount != 0 && generatedKeys.next())
      {
        acc.id = generatedKeys.getLong(1);
        s.close();
        cacheByEmail.put(acc.email, acc);
        return true;
      }
    }
    catch (MySQLIntegrityConstraintViolationException e)
    {
      if (e.toString().contains("'email'")) throw new EmailAlreadyExistsException();
      else if (e.toString().contains("'username'")) throw new UsernameAlreadyExistsException();
      else
      {
        UnexpectedException ue = new UnexpectedException(e.toString());
        ue.setStackTrace(e.getStackTrace());
        throw ue;
      }
    }
    catch (Exception e)
    {
      UnexpectedException ue = new UnexpectedException(e.toString());
      ue.setStackTrace(e.getStackTrace());
      throw ue;
    }
    throw new UnexpectedException("Error while trying to create account, rowcount was 0 or no id could be found.");
  }

  public static boolean update(Account acc) throws UnexpectedException, AccountNotFoundException
  {
    try
    {
      PreparedStatement s1 = Database.getConnection()
          .prepareStatement("UPDATE Accounts SET username = ?, password = ?, email = ?, pw_reset_token = ?, failed_login_attempts = ? WHERE id = ?;");
      int i = 1;
      s1.setString(i++, acc.username);
      s1.setString(i++, acc.password);
      s1.setString(i++, acc.email);
      s1.setString(i++, acc.pwResetToken);
      s1.setInt(i++, acc.failedLoginAttempts);
      s1.setLong(i++, acc.id);
      int rowCount = s1.executeUpdate();
      s1.close();

      if (rowCount == 1)
      {
        cacheByEmail.put(acc.email, acc);
        return true;
      }
    }
    catch (Exception e)
    {
      UnexpectedException ue = new UnexpectedException(e.toString());
      ue.setStackTrace(e.getStackTrace());
      throw ue;
    }
    throw new AccountNotFoundException();
  }

  // Optimized specific methods
  public static boolean setPasswordResetToken(String email, String pwResetToken) throws UnexpectedException, AccountNotFoundException
  {
    try
    {
      PreparedStatement s1 = Database.getConnection().prepareStatement("UPDATE Accounts SET pw_reset_token = ? WHERE email = ?;");
      s1.setString(1, pwResetToken);
      s1.setString(2, email);
      int rowCount = s1.executeUpdate();
      s1.close();

      if (rowCount == 1)
      {
        Account cachedAccount = cacheByEmail.get(email);
        if (cachedAccount != null)
        {
          cachedAccount.pwResetToken = pwResetToken;
          cacheByEmail.put(email, cachedAccount);
        }
        return true;
      }
    }
    catch (Exception e)
    {
      UnexpectedException ue = new UnexpectedException(e.toString());
      ue.setStackTrace(e.getStackTrace());
      throw ue;
    }
    throw new AccountNotFoundException();
  }

  public static boolean checkAuthToken(String email, String authToken)
  {
    Account acc = cacheByEmail.get(email);
    if (acc != null && acc.authToken.equals(authToken)) return true;
    return false;
  }

  public static Long checkAuthTokenAndGetAccId(String email, String authToken) throws WrongAuthentificationTokenException
  {
    Account acc = cacheByEmail.get(email);
    if (acc != null && acc.authToken.equals(authToken)) return acc.id;
    throw new WrongAuthentificationTokenException();
  }

  public static void updateCacheOnly(Account acc)
  {
    cacheByEmail.put(acc.email, acc);
  }

  public static void clearCache()
  {
    cacheByEmail.clear();
  }
}
