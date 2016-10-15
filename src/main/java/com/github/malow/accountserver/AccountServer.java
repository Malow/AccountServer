package com.github.malow.accountserver;

import com.github.malow.accountserver.database.AccountAccessor;
import com.github.malow.accountserver.database.AccountAccessor.WrongAuthentificationTokenException;
import com.github.malow.accountserver.database.Database;
import com.github.malow.accountserver.handlers.EmailHandler;
import com.github.malow.accountserver.httpsapi.HttpsApiServer;
import com.github.malow.malowlib.MaloWLogger;

/**
 * 
 * How to use: Create an AccountServerConfig object, populate it with all config, call AccountServer.start(config). A thread will be created that runs the
 * HTTPS-server API and your thread will return for you to do whatever with. Register, Login and ResetPW requests will all return responses that contains an
 * authToken. This authToken should then be used to make further authorized requests which your program will handle. To check the authentication of these
 * request simply call CheckAuthentication on this class sending in the email and authToken, and it will return the accountId that you can then use as foreign
 * keys etc. for your tables. If the authentication fails an exception will be thrown. Finally when you're done simply call AccountServer.close() to shut it all
 * down.
 *
 */
public class AccountServer
{
  private static HttpsApiServer httpsApiServer;

  public static void start(AccountServerConfig config)
  {
    EmailHandler.init(config.gmailUsername, config.gmailPassword);
    Database.init(config.databaseName, config.databaseUser, config.databasePassword);

    MaloWLogger.info("Starting GladiatorManagerServer in directory " + System.getProperty("user.dir") + " using port " + config.httpsApiPort);
    httpsApiServer = new HttpsApiServer();
    httpsApiServer.start(config);
  }

  public static Long checkAuthentication(String email, String authToken) throws WrongAuthentificationTokenException
  {
    Long accId = AccountAccessor.checkAuthTokenAndGetAccId(email, authToken);
    return accId;
  }

  public static void close()
  {
    httpsApiServer.close();
    Database.close();

    MaloWLogger.info("Server closed successfully");
  }
}
