package com.github.malow.accountserver.comstructs.account;

import com.github.malow.accountserver.comstructs.Request;

public class RegisterRequest extends Request
{
  public String password;

  public RegisterRequest(String email, String password)
  {
    super(email);
    this.password = password;
  }

  @Override
  public boolean isValid()
  {
    if (super.isValid() && this.password != null)
    {
      return true;
    }
    return false;
  }
}
