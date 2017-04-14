package com.github.malow.accountserver.database;

import com.github.malow.malowlib.database.DatabaseTableEntity;

public class Account extends DatabaseTableEntity
{
  // Persisted in database
  @Unique
  public String username;
  public String password;
  @Unique
  public String email;
  @Optional
  public String pwResetToken;
  public Integer failedLoginAttempts = 0;

  // Only cached in memory
  @NotPersisted
  public String authToken;
}
