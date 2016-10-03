package com.github.malow.accountserver.account;

import java.util.UUID;

import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.Response;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.LoginResponse;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.github.malow.accountserver.database.AccountAccessor;
import com.github.malow.accountserver.database.AccountAccessor.AccountNotFoundException;
import com.github.malow.accountserver.database.AccountAccessor.EmailAlreadyExistsException;
import com.github.malow.accountserver.database.AccountAccessor.UsernameAlreadyExistsException;
import com.github.malow.accountserver.database.Database.UnexpectedException;
import com.github.malow.accountserver.handlers.EmailHandler;
import com.github.malow.accountserver.handlers.PasswordHandler;

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
        acc.failedLoginAttempts = 0;
        AccountAccessor.update(acc);
        return new LoginResponse(true, authToken);
      }
      acc.failedLoginAttempts += 1;
      AccountAccessor.update(acc);
      return new ErrorResponse(false, "Wrong account details");
    }
    catch (AccountNotFoundException e)
    {
      return new ErrorResponse(false, "Wrong account details");
    }
    catch (UnexpectedException e)
    {
      System.out.println("Unexpected error when trying to login: " + e.toString());
      e.printStackTrace();
      return new ErrorResponse(false, "Unexpected error");
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
      return new ErrorResponse(false, "Email already in use");
    }
    catch (UsernameAlreadyExistsException e)
    {
      return new ErrorResponse(false, "Username already taken");
    }
    catch (UnexpectedException e)
    {
      System.out.println("Unexpected Database error when trying to register: " + e.error);
      e.printStackTrace();
      return new ErrorResponse(false, "Unexpected error");
    }
    catch (Exception e)
    {
      System.out.println("Unexpected exception when trying to register: " + e.toString());
      e.printStackTrace();
      return new ErrorResponse(false, "Unexpected error");
    }
  }

  public static Response sendPasswordResetToken(Request req)
  {
    try
    {
      String pwResetToken = UUID.randomUUID().toString();
      AccountAccessor.setPasswordResetToken(req.email, pwResetToken);
      EmailHandler.sendMail(req.email, "Your password reset for GladiatorManager", pwResetToken);
      return new Response(true);
    }
    catch (AccountNotFoundException e)
    {
      return new ErrorResponse(false, "No account found for that email-address");
    }
    catch (UnexpectedException e)
    {
      System.out.println("Unexpected error when trying to sendPasswordResetToken: " + e.toString());
      e.printStackTrace();
      return new ErrorResponse(false, "Unexpected error");
    }
  }

  public static Response resetPassword(ResetPasswordRequest req)
  {
    try
    {
      Account acc = AccountAccessor.read(req.email);
      if (acc.pwResetToken.equals(req.pwResetToken))
      {
        String authToken = UUID.randomUUID().toString();
        acc.authToken = authToken;
        acc.password = PasswordHandler.hashPassword(req.password);
        acc.pwResetToken = null;
        AccountAccessor.update(acc);
        return new LoginResponse(true, authToken);
      }
      return new ErrorResponse(false, "Bad password-reset token.");
    }
    catch (AccountNotFoundException e)
    {
      return new ErrorResponse(false, "No account found for that email-address");
    }
    catch (UnexpectedException e)
    {
      System.out.println("Unexpected error when trying to resetPassword: " + e.toString());
      e.printStackTrace();
      return new ErrorResponse(false, "Unexpected error");
    }
  }
}