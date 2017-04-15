package com.github.malow.accountserver.database;

import com.github.malow.malowlib.database.DatabaseConnection;

public enum AccountAccessorSingleton
{
  INSTANCE;

  private AccountAccessor accountAccessor;

  public static AccountAccessor get()
  {
    return INSTANCE.accountAccessor;
  }

  public static void init(DatabaseConnection databaseConnection)
  {
    INSTANCE.accountAccessor = new AccountAccessor(databaseConnection);
  }
}
