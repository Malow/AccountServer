package com.github.malow.accountserver.database;

import com.github.malow.malowlib.database.DatabaseTableEntity;

public class Account extends DatabaseTableEntity
{
  // Persisted in database
  @Unique
  public String email;
  public String password;
  public Integer failedLoginAttempts = 0;
  @Optional
  public String pwResetToken;

  // Only cached in memory
  @NotPersisted
  public String authToken;
}
