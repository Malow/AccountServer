package com.github.malow.accountserver.comstructs;

import com.github.malow.malowlib.network.https.HttpsPostRequest;

public class Request implements HttpsPostRequest
{
  public String email;

  public Request(String email)
  {
    this.email = email;
  }

  @Override
  public boolean isValid()
  {
    if (this.email != null) return true;

    return false;
  }
}
