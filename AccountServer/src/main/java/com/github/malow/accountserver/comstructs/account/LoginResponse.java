package com.github.malow.accountserver.comstructs.account;

import com.github.malow.accountserver.comstructs.Response;

public class LoginResponse extends Response
{
  public String authToken;

  public LoginResponse(boolean result, String authToken)
  {
    super(result);
    this.authToken = authToken;
  }
}
