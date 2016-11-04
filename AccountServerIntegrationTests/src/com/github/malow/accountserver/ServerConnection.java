package com.github.malow.accountserver;

import com.github.malow.malowlib.network.https.HttpsPostClient;

public class ServerConnection
{
  public static String register(String email, String username, String password) throws Exception
  {
    String request = JsonRequests.register(email, username, password);
    return HttpsPostClient.sendMessage("/register", request);
  }

  public static String login(String email, String password) throws Exception
  {
    String request = JsonRequests.login(email, password);
    return HttpsPostClient.sendMessage("/login", request);
  }

  public static String sendPasswordResetToken(String email) throws Exception
  {
    String request = JsonRequests.sendPasswordResetToken(email);
    return HttpsPostClient.sendMessage("/sendpwresettoken", request);
  }

  public static String resetPassword(String email, String password, String pwResetToken) throws Exception
  {
    String request = JsonRequests.resetPassword(email, password, pwResetToken);
    return HttpsPostClient.sendMessage("/resetpw", request);
  }

  public static void clearCache() throws Exception
  {
    HttpsPostClient.sendMessage("/clearcache", "");
  }
}
