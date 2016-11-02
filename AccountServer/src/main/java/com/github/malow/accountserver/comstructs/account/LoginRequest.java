package com.github.malow.accountserver.comstructs.account;

import com.github.malow.accountserver.comstructs.Request;

public class LoginRequest extends Request
{
  public String password;

  public LoginRequest(String email, String password)
  {
    super(email);
    this.password = password;
  }

  @Override
  public boolean isValid()
  {
    if (super.isValid() && this.password != null) return true;

    return false;
  }
}
