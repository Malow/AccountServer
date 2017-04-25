package com.github.malow.accountserver;

import com.github.malow.accountserver.database.AccountAccessor.WrongAuthentificationTokenException;
import com.github.malow.accountserver.database.AccountAccessorSingleton;
import com.github.malow.accountserver.handlers.EmailHandler;
import com.github.malow.accountserver.handlers.HttpsApiHandlers.ClearCacheHandler;
import com.github.malow.accountserver.handlers.HttpsApiHandlers.LoginHandler;
import com.github.malow.accountserver.handlers.HttpsApiHandlers.RegisterHandler;
import com.github.malow.accountserver.handlers.HttpsApiHandlers.ResetPasswordHandler;
import com.github.malow.accountserver.handlers.HttpsApiHandlers.SendPasswordResetTokenHandler;
import com.github.malow.accountserver.handlers.HttpsApiHandlers.TestHandler;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.network.https.HttpsPostServer;

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
  public static void start(AccountServerConfig config, HttpsPostServer httpsServer)
  {
    EmailHandler.init(config.gmailUsername, config.gmailPassword, config.appName, config.enableEmailSending);
    AccountAccessorSingleton.init(config.databaseConnection);

    MaloWLogger.info("Starting AccountServer for " + config.appName + " in directory " + System.getProperty("user.dir") + " using "
        + config.httpsRootPath + " as root path.");
    httpsServer.createContext(config.httpsRootPath + config.testPath, new TestHandler());
    httpsServer.createContext(config.httpsRootPath + config.loginPath, new LoginHandler());
    httpsServer.createContext(config.httpsRootPath + config.registerPath, new RegisterHandler());
    httpsServer.createContext(config.httpsRootPath + config.sendPwResetTokenPath, new SendPasswordResetTokenHandler());
    httpsServer.createContext(config.httpsRootPath + config.resetPwPath, new ResetPasswordHandler());
    if (config.allowClearCacheOperation)
    {
      httpsServer.createContext(config.httpsRootPath + config.clearCachePath, new ClearCacheHandler());
    }
  }

  public static Integer checkAuthentication(String email, String authToken) throws WrongAuthentificationTokenException
  {
    Integer accId = AccountAccessorSingleton.get().checkAuthTokenAndGetAccId(email, authToken);
    return accId;
  }

  public static void close()
  {
    MaloWLogger.info("AccountServer closed successfully");
  }
}
