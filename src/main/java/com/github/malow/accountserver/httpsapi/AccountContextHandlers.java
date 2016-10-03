package com.github.malow.accountserver.httpsapi;

import static com.github.malow.accountserver.httpsapi.ContextHelpers.getValidRequest;
import static com.github.malow.accountserver.httpsapi.ContextHelpers.sendMessage;

import com.github.malow.accountserver.account.AccountHandler;
import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.Response;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AccountContextHandlers
{
  static class LoginHandler implements HttpHandler
  {
    @Override
    public void handle(HttpExchange t)
    {
      LoginRequest req = (LoginRequest) getValidRequest(t, LoginRequest.class);
      if (req != null)
      {
        Response resp = AccountHandler.login(req);
        sendMessage(t, 200, new Gson().toJson(resp));
      }
      else
      {
        sendMessage(t, 200, new Gson().toJson(new ErrorResponse(false, "Request has wrong parameters")));
      }
    }
  }

  static class RegisterHandler implements HttpHandler
  {
    @Override
    public void handle(HttpExchange t)
    {
      RegisterRequest req = (RegisterRequest) getValidRequest(t, RegisterRequest.class);
      if (req != null)
      {
        Response resp = AccountHandler.register(req);
        sendMessage(t, 200, new Gson().toJson(resp));
      }
      else
      {
        sendMessage(t, 200, new Gson().toJson(new ErrorResponse(false, "Request has wrong parameters")));
      }
    }
  }

  static class SendPasswordResetTokenHandler implements HttpHandler
  {
    @Override
    public void handle(HttpExchange t)
    {
      Request req = getValidRequest(t, Request.class);
      if (req != null)
      {
        Response resp = AccountHandler.sendPasswordResetToken(req);
        sendMessage(t, 200, new Gson().toJson(resp));
      }
      else
      {
        sendMessage(t, 200, new Gson().toJson(new ErrorResponse(false, "Request has wrong parameters")));
      }
    }
  }

  static class ResetPasswordHandler implements HttpHandler
  {
    @Override
    public void handle(HttpExchange t)
    {
      ResetPasswordRequest req = (ResetPasswordRequest) getValidRequest(t, ResetPasswordRequest.class);
      if (req != null)
      {
        Response resp = AccountHandler.resetPassword(req);
        sendMessage(t, 200, new Gson().toJson(resp));
      }
      else
      {
        sendMessage(t, 200, new Gson().toJson(new ErrorResponse(false, "Request has wrong parameters")));
      }
    }
  }
}
