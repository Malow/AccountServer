package com.github.malow.accountserver;

public class AccountServerConfig
{
  public String databaseName;
  public String databaseUser;
  public String databasePassword;

  public int httpsApiPort;
  public String httpsApiCertPassword;
  public String httpsApiTestPath = "/test";
  public String httpsApiLoginPath = "/login";
  public String httpsApiRegisterPath = "/register";
  public String httpsApiSendPwResetTokenPath = "/sendpwresettoken";
  public String httpsApiResetPwPath = "/resetpw";
  public String httpsApiClearCachePath = "/clearcache";

  public boolean enableEmailSending = true;
  public String gmailUsername;
  public String gmailPassword;
  public String appName;

  public boolean allowClearCacheOperation = false;
}