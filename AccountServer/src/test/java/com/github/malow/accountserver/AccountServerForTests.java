package com.github.malow.accountserver;

import java.util.Scanner;

import org.junit.Test;

import com.github.malow.accountserver.handlers.AccountHandler;
import com.github.malow.malowlib.database.DatabaseConnection;
import com.github.malow.malowlib.database.DatabaseConnection.DatabaseType;
import com.github.malow.malowlib.network.https.HttpsPostServerConfig;
import com.github.malow.malowlib.network.https.HttpsPostServerConfig.JksFileConfig;

public class AccountServerForTests
{
  @Test
  public void runForIntegrationTests()
  {
    HttpsPostServerConfig httpsConfig = new HttpsPostServerConfig(7000, new JksFileConfig("https_key.jks"), "password");
    httpsConfig.useMultipleThreads = false;
    AccountServerConfig config = new AccountServerConfig(DatabaseConnection.get(DatabaseType.SQLITE_FILE, "AccountServer"), httpsConfig,
        "gladiatormanager.noreply", "passwordFU", "AccountServerTest");

    config.enableEmailSending = false;
    config.allowClearCacheOperation = true;

    AccountServer.start(config);
    AccountHandler.accountAccessor.createTable();

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
