package com.github.malow.accountserver.handlers;

import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.Response;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.github.malow.accountserver.database.AccountAccessor;
import com.github.malow.malowlib.network.https.HttpsPostHandler;
import com.google.gson.Gson;

public class HttpsApiHandlers
{
  public static class TestHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request)
    {
      return new Gson().toJson(new Response(true));
    }
  }

  public static class ClearCacheHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request)
    {
      AccountAccessor.clearCache();
      return new Gson().toJson(new Response(true));
    }
  }

  public static class LoginHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request)
    {
      LoginRequest req = (LoginRequest) createValidJsonRequest(request, LoginRequest.class);
      if (req != null)
      {
        Response resp = AccountHandler.login(req);
        return new Gson().toJson(resp);
      }
      else
      {
        return new Gson().toJson(new ErrorResponse(false, "Request has wrong parameters"));
      }
    }
  }

  public static class RegisterHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request)
    {
      RegisterRequest req = (RegisterRequest) createValidJsonRequest(request, RegisterRequest.class);
      if (req != null)
      {
        Response resp = AccountHandler.register(req);
        return new Gson().toJson(resp);
      }
      else
      {
        return new Gson().toJson(new ErrorResponse(false, "Request has wrong parameters"));
      }
    }
  }

  public static class SendPasswordResetTokenHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request)
    {
      Request req = (Request) createValidJsonRequest(request, Request.class);
      if (req != null)
      {
        Response resp = AccountHandler.sendPasswordResetToken(req);
        return new Gson().toJson(resp);
      }
      else
      {
        return new Gson().toJson(new ErrorResponse(false, "Request has wrong parameters"));
      }
    }
  }

  public static class ResetPasswordHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request)
    {
      ResetPasswordRequest req = (ResetPasswordRequest) createValidJsonRequest(request, ResetPasswordRequest.class);
      if (req != null)
      {
        Response resp = AccountHandler.resetPassword(req);
        return new Gson().toJson(resp);
      }
      else
      {
        return new Gson().toJson(new ErrorResponse(false, "Request has wrong parameters"));
      }
    }
  }
}
