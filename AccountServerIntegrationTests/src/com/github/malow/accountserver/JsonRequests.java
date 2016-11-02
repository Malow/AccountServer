package com.github.malow.accountserver;

import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.google.gson.Gson;

public class JsonRequests
{
  public static String register(String email, String username, String password)
  {
    return new Gson().toJson(new RegisterRequest(email, username, password));
  }

  public static String login(String email, String password)
  {
    return new Gson().toJson(new LoginRequest(email, password));
  }

  public static String sendPasswordResetToken(String email)
  {
    return new Gson().toJson(new Request(email));
  }

  public static String resetPassword(String email, String password, String pwResetToken)
  {
    return new Gson().toJson(new ResetPasswordRequest(email, password, pwResetToken));
  }
}
