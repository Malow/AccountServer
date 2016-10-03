package com.github.malow.accountserver;

import java.util.Scanner;

import org.junit.Test;

public class AccountServerTest
{
  @Test
  public void testRunningServer()
  {
    AccountServerConfig config = new AccountServerConfig();
    config.databaseName = "AccountServer";
    config.databaseUser = "AccServUsr";
    config.databasePassword = "password";
    config.httpsApiCertPassword = "password";
    config.httpsApiPort = 7000;

    config.gmailUsername = "gladiatormanager.noreply";
    config.gmailPassword = "passwordFU";

    AccountServer.start(config);

    String input = "";
    Scanner in = new Scanner(System.in);
    while (!input.equals("Exit"))
    {
      System.out.print("> ");
      input = in.next();
    }
    in.close();

    AccountServer.close();
  }
}
