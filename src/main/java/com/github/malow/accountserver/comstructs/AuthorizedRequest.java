package com.github.malow.accountserver.comstructs;

import com.github.malow.accountserver.database.AccountAccessor;

public class AuthorizedRequest extends Request
{
  public String authToken;

  public AuthorizedRequest(String email, String authToken)
  {
    super(email);
    this.authToken = authToken;
  }

  private boolean isAuthorized()
  {
    if (AccountAccessor.checkAuthToken(this.email, this.authToken)) return true;
    System.out.println("Un-Authorized request recived for: " + this.email);
    return false;
  }

  @Override
  public boolean isValid()
  {
    if (super.isValid() && this.authToken != null && this.isAuthorized()) return true;
    return false;
  }

}
