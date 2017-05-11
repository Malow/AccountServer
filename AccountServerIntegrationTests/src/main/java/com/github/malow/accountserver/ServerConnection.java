package com.github.malow.accountserver;

import com.github.malow.malowlib.network.https.HttpsPostClient;

public class ServerConnection
{
  public static final HttpsPostClient httpsPostClient = new HttpsPostClient(Config.HOST, true);

  public static String register(String email, String password) throws Exception
  {
    String request = JsonRequests.register(email, password);
    return httpsPostClient.sendMessage("/account/register", request);
  }

  public static String login(String email, String password) throws Exception
  {
    String request = JsonRequests.login(email, password);
    return httpsPostClient.sendMessage("/account/login", request);
  }

  public static String sendPasswordResetToken(String email) throws Exception
  {
    String request = JsonRequests.sendPasswordResetToken(email);
    return httpsPostClient.sendMessage("/account/sendpwresettoken", request);
  }

  public static String resetPassword(String email, String password, String pwResetToken) throws Exception
  {
    String request = JsonRequests.resetPassword(email, password, pwResetToken);
    return httpsPostClient.sendMessage("/account/resetpw", request);
  }

  public static void clearCache() throws Exception
  {
    httpsPostClient.sendMessage("/account/clearcache", "{}");
  }
}
