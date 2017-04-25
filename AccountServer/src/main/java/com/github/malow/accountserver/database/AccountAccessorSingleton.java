package com.github.malow.accountserver.database;

import com.github.malow.malowlib.database.DatabaseConnection;

public enum AccountAccessorSingleton
{
  INSTANCE;

  private AccountAccessor accessor;

  public static AccountAccessor get()
  {
    return INSTANCE.accessor;
  }

  public static void init(DatabaseConnection databaseConnection)
  {
    INSTANCE.accessor = new AccountAccessor(databaseConnection);
  }
}
