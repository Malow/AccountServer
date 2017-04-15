package com.github.malow.accountserver.comstructs;

import com.github.malow.accountserver.database.AccountAccessor.WrongAuthentificationTokenException;
import com.github.malow.accountserver.database.AccountAccessorSingleton;

public class AuthorizedRequest extends Request
{
  public String authToken;
  public Integer accountId;

  public AuthorizedRequest(String email, String authToken)
  {
    super(email);
    this.authToken = authToken;
  }

  private boolean isAuthorized()
  {
    try
    {
      this.accountId = AccountAccessorSingleton.get().checkAuthTokenAndGetAccId(this.email, this.authToken);
      return true;
    }
    catch (WrongAuthentificationTokenException e)
    {
      return false;
    }
  }

  @Override
  public boolean isValid()
  {
    if (super.isValid() && this.authToken != null && this.isAuthorized())
    {
      return true;
    }
    return false;
  }

}
