package com.github.malow.accountserver.handlers;

import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.Response;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.github.malow.accountserver.database.AccountAccessorSingleton;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.network.https.HttpsJsonPostHandler;
import com.github.malow.malowlib.network.https.HttpsPostResponse;
import com.github.malow.malowlib.network.https.HttpsPostTestRequest;

public class HttpsApiHandlers
{
  public static class TestHandler extends HttpsJsonPostHandler<HttpsPostTestRequest>
  {
    @Override
    public HttpsPostResponse handleRequestAndGetResponse(HttpsPostTestRequest request)
    {
      MaloWLogger.info("AccountServer TestRequest received.");
      return new Response(true);
    }
  }

  public static class ClearCacheHandler extends HttpsJsonPostHandler<HttpsPostTestRequest>
  {
    @Override
    public HttpsPostResponse handleRequestAndGetResponse(HttpsPostTestRequest request)
    {
      AccountAccessorSingleton.get().clearCache();
      MaloWLogger.info("AccountServer cache cleared.");
      return new Response(true);
    }
  }

  public static class LoginHandler extends HttpsJsonPostHandler<LoginRequest>
  {
    @Override
    public HttpsPostResponse handleRequestAndGetResponse(LoginRequest request) throws BadRequestException
    {
      return AccountHandler.login(request);
    }
  }

  public static class RegisterHandler extends HttpsJsonPostHandler<RegisterRequest>
  {
    @Override
    public HttpsPostResponse handleRequestAndGetResponse(RegisterRequest request) throws BadRequestException
    {
      return AccountHandler.register(request);
    }
  }

  public static class SendPasswordResetTokenHandler extends HttpsJsonPostHandler<Request>
  {
    @Override
    public HttpsPostResponse handleRequestAndGetResponse(Request request) throws BadRequestException
    {
      return AccountHandler.sendPasswordResetToken(request);
    }
  }

  public static class ResetPasswordHandler extends HttpsJsonPostHandler<ResetPasswordRequest>
  {
    @Override
    public HttpsPostResponse handleRequestAndGetResponse(ResetPasswordRequest request) throws BadRequestException
    {
      return AccountHandler.resetPassword(request);
    }
  }
}
