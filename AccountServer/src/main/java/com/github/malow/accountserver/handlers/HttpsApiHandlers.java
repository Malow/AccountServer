package com.github.malow.accountserver.handlers;

import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.Response;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.github.malow.accountserver.database.AccountAccessor;
import com.github.malow.malowlib.GsonSingleton;
import com.github.malow.malowlib.network.https.HttpsPostHandler;

public class HttpsApiHandlers
{
  public static class TestHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request)
    {
      return GsonSingleton.get().toJson(new Response(true));
    }
  }

  public static class ClearCacheHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request)
    {
      AccountAccessor.clearCache();
      return GsonSingleton.get().toJson(new Response(true));
    }
  }

  public static class LoginHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request) throws BadRequestException
    {
      LoginRequest req = (LoginRequest) createValidJsonRequest(request, LoginRequest.class);
      Response resp = AccountHandler.login(req);
      return GsonSingleton.get().toJson(resp);
    }
  }

  public static class RegisterHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request) throws BadRequestException
    {
      RegisterRequest req = (RegisterRequest) createValidJsonRequest(request, RegisterRequest.class);
      Response resp = AccountHandler.register(req);
      return GsonSingleton.get().toJson(resp);
    }
  }

  public static class SendPasswordResetTokenHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request) throws BadRequestException
    {
      Request req = (Request) createValidJsonRequest(request, Request.class);
      Response resp = AccountHandler.sendPasswordResetToken(req);
      return GsonSingleton.get().toJson(resp);
    }
  }

  public static class ResetPasswordHandler extends HttpsPostHandler
  {
    @Override
    public String handleRequestAndGetResponse(String request) throws BadRequestException
    {
      ResetPasswordRequest req = (ResetPasswordRequest) createValidJsonRequest(request, ResetPasswordRequest.class);
      Response resp = AccountHandler.resetPassword(req);
      return GsonSingleton.get().toJson(resp);
    }
  }
}
