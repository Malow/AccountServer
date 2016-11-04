package com.github.malow.accountserver;

import com.github.malow.malowlib.network.https.HttpsPostServerConfig;

public class AccountServerConfig
{
  public String databaseName;
  public String databaseUser;
  public String databasePassword;

  public HttpsPostServerConfig httpsConfig;

  public String gmailUsername;
  public String gmailPassword;
  public String appName;

  public boolean enableEmailSending = true;
  public boolean allowClearCacheOperation = false;

  public AccountServerConfig(String databaseName, String databaseUser, String databasePassword, HttpsPostServerConfig httpsConfig,
      String gmailUsername, String gmailPassword, String appName)
  {
    this.databaseName = databaseName;
    this.databaseUser = databaseUser;
    this.databasePassword = databasePassword;
    this.httpsConfig = httpsConfig;
    this.gmailUsername = gmailUsername;
    this.gmailPassword = gmailPassword;
    this.appName = appName;
  }
}
