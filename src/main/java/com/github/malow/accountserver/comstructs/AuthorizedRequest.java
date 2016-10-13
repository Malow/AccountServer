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
    return AccountAccessor.checkAuthToken(this.email, this.authToken);
  }

  @Override
  public boolean isValid()
  {
    if (super.isValid() && this.authToken != null && this.isAuthorized()) return true;
    return false;
  }

}
