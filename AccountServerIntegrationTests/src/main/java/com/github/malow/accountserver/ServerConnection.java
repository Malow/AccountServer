package com.github.malow.accountserver;

import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.github.malow.malowlib.network.https.HttpPostClient;

public class ServerConnection
{
  public static final HttpPostClient httpsPostClient = new HttpPostClient(Config.HOST, true);

  public static String register(String email, String password) throws Exception
  {
    return httpsPostClient.sendMessage("/account/register", new RegisterRequest(email, password));
  }

  public static String login(String email, String password) throws Exception
  {
    return httpsPostClient.sendMessage("/account/login", new LoginRequest(email, password));
  }

  public static String sendPasswordResetToken(String email) throws Exception
  {
    return httpsPostClient.sendMessage("/account/sendpwresettoken", new Request(email));
  }

  public static String resetPassword(String email, String password, String pwResetToken) throws Exception
  {
    return httpsPostClient.sendMessage("/account/resetpw", new ResetPasswordRequest(email, password, pwResetToken));
  }

  public static void clearCache() throws Exception
  {
    httpsPostClient.sendMessage("/account/clearcache", "{}");
  }
}
