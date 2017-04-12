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
import com.github.malow.accountserver.database.AccountAccessor.AccountNotFoundException;
import com.github.malow.accountserver.database.AccountAccessor.EmailAlreadyExistsException;
import com.github.malow.accountserver.database.AccountAccessor.UsernameAlreadyExistsException;
import com.github.malow.accountserver.database.Database.UnexpectedException;
import com.github.malow.malowlib.MaloWLogger;

public class AccountHandler
{
  public static Response login(LoginRequest req)
  {
    try
    {
      Account acc = AccountAccessor.read(req.email);
      if (PasswordHandler.checkPassword(req.password, acc.password))
      {
        String authToken = UUID.randomUUID().toString();
        acc.authToken = authToken;
        if (acc.failedLoginAttempts == 0)
        {
          AccountAccessor.updateCacheOnly(acc);
        }
        else
        {
          acc.failedLoginAttempts = 0;
          AccountAccessor.update(acc);
        }
        return new LoginResponse(true, authToken);
      }
      acc.failedLoginAttempts += 1;
      AccountAccessor.update(acc);
      return new ErrorResponse(false, ErrorMessages.WRONG_PASSWORD);
    }
    catch (AccountNotFoundException e)
    {
      return new ErrorResponse(false, ErrorMessages.EMAIL_NOT_REGISTERED);
    }
    catch (UnexpectedException e)
    {
      MaloWLogger.error("Unexpected error when trying to login", e);
      return new ErrorResponse(false, ErrorMessages.UNEXPECTED_ERROR);
    }
  }

  public static Response register(RegisterRequest req)
  {
    try
    {
      String authToken = UUID.randomUUID().toString();
      Account acc = new Account();
      acc.id = null;
      acc.username = req.username;
      acc.password = PasswordHandler.hashPassword(req.password);
      acc.email = req.email;
      acc.pwResetToken = null;
      acc.failedLoginAttempts = 0;
      acc.authToken = authToken;

      AccountAccessor.create(acc);
      return new LoginResponse(true, authToken);
    }
    catch (EmailAlreadyExistsException e)
    {
      return new ErrorResponse(false, ErrorMessages.EMAIL_TAKEN);
    }
    catch (UsernameAlreadyExistsException e)
    {
      return new ErrorResponse(false, ErrorMessages.USERNAME_TAKEN);
    }
    catch (UnexpectedException e)
    {
      MaloWLogger.error("Unexpected Database error when trying to register", e);
      return new ErrorResponse(false, ErrorMessages.UNEXPECTED_ERROR);
    }
    catch (Exception e)
    {
      MaloWLogger.error("Unexpected exception when trying to register", e);
      return new ErrorResponse(false, ErrorMessages.UNEXPECTED_ERROR);
    }
  }

  public static Response sendPasswordResetToken(Request req)
  {
    try
    {
      String pwResetToken = UUID.randomUUID().toString();
      AccountAccessor.setPasswordResetToken(req.email, pwResetToken);
      EmailHandler.sendPasswordResetTokenMail(req.email, pwResetToken);
      return new Response(true);
    }
    catch (AccountNotFoundException e)
    {
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
    try
    {
      Account acc = AccountAccessor.read(req.email);
      if (acc.pwResetToken != null && acc.pwResetToken.equals(req.pwResetToken))
      {
        String authToken = UUID.randomUUID().toString();
        acc.authToken = authToken;
        acc.password = PasswordHandler.hashPassword(req.password);
        acc.pwResetToken = null;
        AccountAccessor.update(acc);
        return new LoginResponse(true, authToken);
      }
      return new ErrorResponse(false, ErrorMessages.BAD_PW_RESET_TOKEN);
    }
    catch (AccountNotFoundException e)
    {
      return new ErrorResponse(false, ErrorMessages.EMAIL_NOT_REGISTERED);
    }
    catch (UnexpectedException e)
    {
      MaloWLogger.error("Unexpected error when trying to resetPassword: ", e);
      return new ErrorResponse(false, ErrorMessages.UNEXPECTED_ERROR);
    }
  }
}
