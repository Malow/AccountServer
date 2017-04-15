package com.github.malow.accountserver;

import com.github.malow.accountserver.comstructs.Request;
import com.github.malow.accountserver.comstructs.account.LoginRequest;
import com.github.malow.accountserver.comstructs.account.RegisterRequest;
import com.github.malow.accountserver.comstructs.account.ResetPasswordRequest;
import com.github.malow.malowlib.GsonSingleton;

public class JsonRequests
{
  public static String register(String email, String password)
  {
    return GsonSingleton.toJson(new RegisterRequest(email, password));
  }

  public static String login(String email, String password)
  {
    return GsonSingleton.toJson(new LoginRequest(email, password));
  }

  public static String sendPasswordResetToken(String email)
  {
    return GsonSingleton.toJson(new Request(email));
  }

  public static String resetPassword(String email, String password, String pwResetToken)
  {
    return GsonSingleton.toJson(new ResetPasswordRequest(email, password, pwResetToken));
  }
}
