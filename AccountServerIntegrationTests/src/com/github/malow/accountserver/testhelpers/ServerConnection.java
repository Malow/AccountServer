package com.github.malow.accountserver.testhelpers;

import com.github.malow.malowlib.network.https.HttpsPostClient;

public class ServerConnection
{
  public static HttpsPostClient httpsPostClient = new HttpsPostClient(Config.HOST);

  public static String register(String email, String username, String password) throws Exception
  {
    String request = JsonRequests.register(email, username, password);
    return httpsPostClient.sendMessage("/register", request);
  }

  public static String login(String email, String password) throws Exception
  {
    String request = JsonRequests.login(email, password);
    return httpsPostClient.sendMessage("/login", request);
  }

  public static String sendPasswordResetToken(String email) throws Exception
  {
    String request = JsonRequests.sendPasswordResetToken(email);
    return httpsPostClient.sendMessage("/sendpwresettoken", request);
  }

  public static String resetPassword(String email, String password, String pwResetToken) throws Exception
  {
    String request = JsonRequests.resetPassword(email, password, pwResetToken);
    return httpsPostClient.sendMessage("/resetpw", request);
  }

  public static void clearCache() throws Exception
  {
    httpsPostClient.sendMessage("/clearcache", "");
  }
}
