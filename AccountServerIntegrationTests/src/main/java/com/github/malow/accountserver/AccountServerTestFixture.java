package com.github.malow.accountserver;

import org.junit.Before;

import com.github.malow.accountserver.database.AccountAccessor;
import com.github.malow.malowlib.database.DatabaseConnection;
import com.github.malow.malowlib.database.DatabaseConnection.DatabaseType;

public class AccountServerTestFixture
{
  private static AccountAccessor accountAccessor = new AccountAccessor(
      DatabaseConnection.get(DatabaseType.SQLITE_FILE, "../AccountServer/AccountServer"));

  public static final String TEST_EMAIL = "tester@test.com";
  public static final String TEST_PASSWORD = "testerpw";
  public static final String TEST_NEW_PASSWORD = "testerpwnew";

  @Before
  public void beforeTest() throws Exception
  {
    this.resetDatabase();
    ServerConnection.clearCache();
  }

  public void resetDatabase() throws Exception
  {
    accountAccessor.createTable();
  }

  public static boolean isValidToken(String token)
  {
    if (token != null && token.length() > 0)
    {
      return true;
    }
    return false;
  }

  public static String getPasswordResetTokenForEmail(String email) throws Exception
  {
    return accountAccessor.readByEmail(email).pwResetToken;
  }
}
