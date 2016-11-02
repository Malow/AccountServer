package com.github.malow.accountserver.comstructs.account;

import com.github.malow.accountserver.comstructs.Request;

public class RegisterRequest extends Request
{
  public String username;
  public String password;

  public RegisterRequest(String email, String username, String password)
  {
    super(email);
    this.username = username;
    this.password = password;
  }

  @Override
  public boolean isValid()
  {
    if (super.isValid() && this.username != null && this.password != null) return true;

    return false;
  }
}
