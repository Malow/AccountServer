package com.github.malow.accountserver.handlers;

import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.Response;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.github.malow.accountserver.database.AccountAccessorSingleton;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.network.https.HttpRequestHandler;
import com.github.malow.malowlib.network.https.HttpResponse;
import com.github.malow.malowlib.network.https.HttpTestRequest;

public class HttpsApiHandlers
{
  public static class TestHandler extends HttpRequestHandler<HttpTestRequest>
  {
    @Override
    public HttpResponse handleRequestAndGetResponse(HttpTestRequest request)
    {
      MaloWLogger.info("AccountServer TestRequest received.");
      return new Response(true);
    }
  }

  public static class ClearCacheHandler extends HttpRequestHandler<HttpTestRequest>
  {
    @Override
    public HttpResponse handleRequestAndGetResponse(HttpTestRequest request)
    {
      AccountAccessorSingleton.get().clearCache();
      MaloWLogger.info("AccountServer cache cleared.");
      return new Response(true);
    }
  }

  public static class LoginHandler extends HttpRequestHandler<LoginRequest>
  {
    @Override
    public HttpResponse handleRequestAndGetResponse(LoginRequest request) throws BadRequestException
    {
      return AccountHandler.login(request);
    }
  }

  public static class RegisterHandler extends HttpRequestHandler<RegisterRequest>
  {
    @Override
    public HttpResponse handleRequestAndGetResponse(RegisterRequest request) throws BadRequestException
    {
      return AccountHandler.register(request);
    }
  }

  public static class SendPasswordResetTokenHandler extends HttpRequestHandler<Request>
  {
    @Override
    public HttpResponse handleRequestAndGetResponse(Request request) throws BadRequestException
    {
      return AccountHandler.sendPasswordResetToken(request);
    }
  }

  public static class ResetPasswordHandler extends HttpRequestHandler<ResetPasswordRequest>
  {
    @Override
    public HttpResponse handleRequestAndGetResponse(ResetPasswordRequest request) throws BadRequestException
    {
      return AccountHandler.resetPassword(request);
    }
  }
}
