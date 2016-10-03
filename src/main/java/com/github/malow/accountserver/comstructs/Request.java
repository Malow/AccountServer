package com.github.malow.accountserver.comstructs;

public class Request
{
  public String email;

  public Request(String email)
  {
    this.email = email;
  }

  public boolean isValid()
  {
    if (this.email != null) return true;

    return false;
  }
}
