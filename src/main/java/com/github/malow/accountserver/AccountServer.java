package com.github.malow.accountserver;

import com.github.malow.accountserver.database.Database;
import com.github.malow.accountserver.handlers.EmailHandler;
import com.github.malow.accountserver.httpsapi.HttpsApiServer;

/**
 * 
 * How to use: Create an AccountServerConfig object, populate it with all config, call AccountServer.start(config). A thread will be created that runs the
 * HTTPS-server API and your thread will return for you to do whatever with. Register, Login and ResetPW requests will all return responses that contains an
 * authToken. This authToken should then be used to make further authorized requests which your program will handle. To check the authentication of these
 * request simply extend or create an AuthorizedRequest object and call isValid() on it, and it will return true or false. Finally when you're done simply call
 * AccountServer.close() to shut it all down.
 *
 */
public class AccountServer
{
  private static HttpsApiServer httpsApiServer;

  public static void start(AccountServerConfig config)
  {
    EmailHandler.init(config.gmailUsername, config.gmailPassword);
    Database.init(config.databaseName, config.databaseUser, config.databasePassword);

    System.out.println("Starting GladiatorManagerServer in directory " + System.getProperty("user.dir"));
    System.out.println("Using port " + config.httpsApiPort);
    httpsApiServer = new HttpsApiServer();
    httpsApiServer.start(config);
  }

  public static void close()
  {
    httpsApiServer.close();
    Database.close();

    System.out.println("Server closed successfully");
  }
}
