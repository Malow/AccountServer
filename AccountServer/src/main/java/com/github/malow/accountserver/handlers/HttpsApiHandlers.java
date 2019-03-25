package com.github.malow.accountserver.handlers;

import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.Response;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.github.malow.accountserver.database.AccountAccessorSingleton;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.network.https.HttpRequestHandler;
import com.github.malow.malowlib.network.https.JsonHttpResponse;
import com.github.malow.malowlib.network.https.HttpTestRequest;

public class HttpsApiHandlers
{
  public static class TestHandler extends HttpRequestHandler<HttpTestRequest>
  {
    @Override
    public JsonHttpResponse handleRequestAndGetResponse(HttpTestRequest request)
    {
      MaloWLogger.info("AccountServer TestRequest received.");
      return new Response(true);
    }
  }

  public static class ClearCacheHandler extends HttpRequestHandler<HttpTestRequest>
  {
    @Override
    public JsonHttpResponse handleRequestAndGetResponse(HttpTestRequest request)
    {
      AccountAccessorSingleton.get().clearCache();
      MaloWLogger.info("AccountServer cache cleared.");
      return new Response(true);
    }
  }

  public static class LoginHandler extends HttpRequestHandler<LoginRequest>
  {
    @Override
    public JsonHttpResponse handleRequestAndGetResponse(LoginRequest request) throws BadRequestException
    {
      return AccountHandler.login(request);
    }
  }

  public static class RegisterHandler extends HttpRequestHandler<RegisterRequest>
  {
    @Override
    public JsonHttpResponse handleRequestAndGetResponse(RegisterRequest request) throws BadRequestException
    {
      return AccountHandler.register(request);
    }
  }

  public static class SendPasswordResetTokenHandler extends HttpRequestHandler<Request>
  {
    @Override
    public JsonHttpResponse handleRequestAndGetResponse(Request request) throws BadRequestException
    {
      return AccountHandler.sendPasswordResetToken(request);
    }
  }

  public static class ResetPasswordHandler extends HttpRequestHandler<ResetPasswordRequest>
  {
    @Override
    public JsonHttpResponse handleRequestAndGetResponse(ResetPasswordRequest request) throws BadRequestException
    {
      return AccountHandler.resetPassword(request);
    }
  }
}
