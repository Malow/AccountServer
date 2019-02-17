package com.github.malow.accountserver;

import java.util.Scanner;

import org.junit.Test;

import com.github.malow.malowlib.database.DatabaseConnection;
import com.github.malow.malowlib.database.DatabaseConnection.DatabaseType;
import com.github.malow.malowlib.network.https.SimpleHttpsServer;
import com.github.malow.malowlib.network.https.SimpleHttpsServerConfig;
import com.github.malow.malowlib.network.https.SimpleHttpsServerConfig.LetsEncryptConfig;

public class AccountServerForTests
{
  @Test
  public void runForIntegrationTests() throws Exception
  {
    SimpleHttpsServerConfig httpsConfig = new SimpleHttpsServerConfig(7000, new LetsEncryptConfig("LetsEncryptCerts"), "password");
    SimpleHttpsServer httpsServer = new SimpleHttpsServer(httpsConfig);
    httpsServer.start();
    AccountServerConfig config = new AccountServerConfig(DatabaseConnection.get(DatabaseType.SQLITE_FILE, "AccountServer"),
        "gladiatormanager.noreply", "passwordFU", "AccountServerTest");

    config.enableEmailSending = false;
    config.allowTestOperations = true;

    AccountServer.start(config, httpsServer);

    String input = "";
    Scanner in = new Scanner(System.in);
    while (!input.toLowerCase().equals("exit"))
    {
      System.out.print("> ");
      input = in.next();
    }
    in.close();
    httpsServer.close();
    AccountServer.close();
  }
}
