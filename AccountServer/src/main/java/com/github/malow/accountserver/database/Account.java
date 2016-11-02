package com.github.malow.accountserver.database;

public class Account
{
  // Persisted in database
  public Long id;
  public String username;
  public String password;
  public String email;
  public String pwResetToken;
  public Integer failedLoginAttempts;

  // Only cached in memory
  public String authToken;

  public Account()
  {

  }

  public Account(Long id, String username, String password, String email, String pwResetToken, Integer failedLoginAttempts, String authToken)
  {
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.pwResetToken = pwResetToken;
    this.failedLoginAttempts = failedLoginAttempts;
    this.authToken = authToken;
  }
}
