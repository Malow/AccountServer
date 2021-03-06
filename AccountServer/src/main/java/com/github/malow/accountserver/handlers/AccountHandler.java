package com.github.malow.accountserver.handlers;

import java.util.UUID;

import com.github.malow.accountserver.ErrorMessages;
import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.Response;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.LoginResponse;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.github.malow.accountserver.database.Account;
import com.github.malow.accountserver.database.AccountAccessor;
import com.github.malow.accountserver.database.AccountAccessorSingleton;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.database.DatabaseExceptions.UnexpectedException;
import com.github.malow.malowlib.database.DatabaseExceptions.UniqueException;
import com.github.malow.malowlib.database.DatabaseExceptions.ZeroRowsReturnedException;
import com.github.malow.malowlib.namedmutex.NamedMutex;

public class AccountHandler
{
  private static AccountAccessor accountAccessor = AccountAccessorSingleton.get();

  public static Response login(LoginRequest req)
  {
    NamedMutex mutex = Account.lockByEmail(req.email);
    try
    {
      Account acc = accountAccessor.readByEmail(req.email);
      if (PasswordHandler.checkPassword(req.password, acc.password))
      {
        String authToken = UUID.randomUUID().toString();
        acc.authToken = authToken;
        if (acc.failedLoginAttempts == 0)
        {
          accountAccessor.updateCacheOnly(acc);
        }
        else
        {
          acc.failedLoginAttempts = 0;
          accountAccessor.update(acc);
        }
        MaloWLogger.info("Login request for " + req.email + " was successful.");
        return new LoginResponse(true, authToken);
      }
      acc.failedLoginAttempts += 1;
      accountAccessor.update(acc);
      MaloWLogger.info("Login request for " + req.email + " but wrong password was provided. " + acc.failedLoginAttempts
          + " failed login attempts since last succesful login.");
      return new ErrorResponse(false, ErrorMessages.WRONG_PASSWORD);
    }
    catch (ZeroRowsReturnedException e)
    {
      MaloWLogger.info("Login request for " + req.email + " failed due to no account found for that email.");
      return new ErrorResponse(false, ErrorMessages.EMAIL_NOT_REGISTERED);
    }
    catch (Exception e)
    {
      MaloWLogger.error("Unexpected error when trying to login", e);
      return new ErrorResponse(false, ErrorMessages.UNEXPECTED_ERROR);
    }
    finally
    {
      mutex.unlock();
    }
  }

  public static Response register(RegisterRequest req)
  {
    try
    {
      String authToken = UUID.randomUUID().toString();
      Account acc = new Account();
      acc.password = PasswordHandler.hashPassword(req.password);
      acc.email = req.email;
      acc.authToken = authToken;

      accountAccessor.create(acc);
      MaloWLogger.info("Register request for " + req.email + " was successful.");
      return new LoginResponse(true, authToken);
    }
    catch (UniqueException e)
    {
      MaloWLogger.info("Register request for " + req.email + " failed due to email already being registered.");
      return new ErrorResponse(false, ErrorMessages.EMAIL_ALREADY_REGISTERED);
    }
    catch (Exception e)
    {
      MaloWLogger.error("Unexpected Database error when trying to register", e);
      return new ErrorResponse(false, ErrorMessages.UNEXPECTED_ERROR);
    }
  }

  public static Response sendPasswordResetToken(Request req)
  {
    try
    {
      String pwResetToken = UUID.randomUUID().toString();
      accountAccessor.updatePasswordResetToken(req.email, pwResetToken);
      EmailHandler.sendPasswordResetTokenMail(req.email, pwResetToken);
      MaloWLogger.info("SendPasswordResetToken request for " + req.email + " was successful.");
      return new Response(true);
    }
    catch (ZeroRowsReturnedException e)
    {
      MaloWLogger.info("SendPasswordResetToken request for " + req.email + " failed due to no account found for that email.");
      return new ErrorResponse(false, ErrorMessages.EMAIL_NOT_REGISTERED);
    }
    catch (UnexpectedException e)
    {
      MaloWLogger.error("Unexpected error when trying to sendPasswordResetToken", e);
      return new ErrorResponse(false, ErrorMessages.UNEXPECTED_ERROR);
    }
  }

  public static Response resetPassword(ResetPasswordRequest req)
  {
    NamedMutex mutex = Account.lockByEmail(req.email);
    try
    {
      Account acc = accountAccessor.readByEmail(req.email);
      if (acc.pwResetToken != null && acc.pwResetToken.equals(req.pwResetToken))
      {
        String authToken = UUID.randomUUID().toString();
        acc.authToken = authToken;
        acc.password = PasswordHandler.hashPassword(req.password);
        acc.pwResetToken = null;
        accountAccessor.update(acc);
        MaloWLogger.info("ResetPassword request for " + req.email + " was successful.");
        return new LoginResponse(true, authToken);
      }
      MaloWLogger.info("ResetPassword request for " + req.email + " failed due to bad passwordResetToken.");
      return new ErrorResponse(false, ErrorMessages.BAD_PW_RESET_TOKEN);
    }
    catch (ZeroRowsReturnedException e)
    {
      MaloWLogger.info("ResetPassword request for " + req.email + " failed due to no account found for that email.");
      return new ErrorResponse(false, ErrorMessages.EMAIL_NOT_REGISTERED);
    }
    catch (Exception e)
    {
      MaloWLogger.error("Unexpected error when trying to resetPassword: ", e);
      return new ErrorResponse(false, ErrorMessages.UNEXPECTED_ERROR);
    }
    finally
    {
      mutex.unlock();
    }
  }
}
