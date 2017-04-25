package com.github.malow.accountserver;

import com.github.malow.malowlib.database.DatabaseConnection;

public class AccountServerConfig
{
  public DatabaseConnection databaseConnection;
  public String databaseUser;
  public String databasePassword;

  public String gmailUsername;
  public String gmailPassword;
  public String appName;

  public boolean enableEmailSending = true;
  public boolean allowClearCacheOperation = false;

  public String httpsRootPath = "/account";
  public String testPath = "/test";
  public String loginPath = "/login";
  public String registerPath = "/register";
  public String sendPwResetTokenPath = "/sendpwresettoken";
  public String resetPwPath = "/resetpw";
  public String clearCachePath = "/clearcache";

  public AccountServerConfig(DatabaseConnection databaseConnection, String gmailUsername, String gmailPassword, String appName)
  {
    this.databaseConnection = databaseConnection;
    this.gmailUsername = gmailUsername;
    this.gmailPassword = gmailPassword;
    this.appName = appName;
  }
}
