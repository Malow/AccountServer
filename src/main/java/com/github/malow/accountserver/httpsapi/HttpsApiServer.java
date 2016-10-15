package com.github.malow.accountserver.httpsapi;

import static com.github.malow.accountserver.httpsapi.ContextHelpers.sendMessage;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import com.github.malow.accountserver.AccountServerConfig;
import com.github.malow.accountserver.comstructs.Response;
import com.github.malow.accountserver.httpsapi.AccountContextHandlers.LoginHandler;
import com.github.malow.accountserver.httpsapi.AccountContextHandlers.RegisterHandler;
import com.github.malow.accountserver.httpsapi.AccountContextHandlers.ResetPasswordHandler;
import com.github.malow.accountserver.httpsapi.AccountContextHandlers.SendPasswordResetTokenHandler;
import com.github.malow.malowlib.MaloWLogger;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

public class HttpsApiServer
{
  static class TestHandler implements HttpHandler
  {
    @Override
    public void handle(HttpExchange t)
    {
      sendMessage(t, 200, new Gson().toJson(new Response(true)));
    }
  }

  private HttpsServer server = null;

  public HttpsApiServer()
  {
  }

  public void start(AccountServerConfig config)
  {
    this.initHttpsServer(config.httpsApiPort, config.httpsApiCertPassword);
    this.server.createContext(config.httpsApiTestPath, new TestHandler());
    this.server.createContext(config.httpsApiLoginPath, new LoginHandler());
    this.server.createContext(config.httpsApiRegisterPath, new RegisterHandler());
    this.server.createContext(config.httpsApiSendPwResetTokenPath, new SendPasswordResetTokenHandler());
    this.server.createContext(config.httpsApiResetPwPath, new ResetPasswordHandler());

    //GameContextHandlers
    this.server.start();
  }

  public void close()
  {
    if (this.server != null) this.server.stop(0);
  }

  public void initHttpsServer(int port, String sslPassword)
  {
    try
    {
      this.server = HttpsServer.create(new InetSocketAddress(port), 0);
      SSLContext sslContext = SSLContext.getInstance("TLS");
      char[] password = sslPassword.toCharArray();
      KeyStore ks = KeyStore.getInstance("JKS");
      FileInputStream fis = new FileInputStream("https_key.jks");
      ks.load(fis, password);
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, password);
      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(ks);
      sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
      this.server.setHttpsConfigurator(new HttpsConfigurator(sslContext)
      {
        @Override
        public void configure(HttpsParameters params)
        {
          try
          {
            SSLContext c = SSLContext.getDefault();
            SSLEngine engine = c.createSSLEngine();
            params.setNeedClientAuth(false);
            params.setCipherSuites(engine.getEnabledCipherSuites());
            params.setProtocols(engine.getEnabledProtocols());
            SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
            params.setSSLParameters(defaultSSLParameters);
          }
          catch (Exception e)
          {
            MaloWLogger.error("Failed to create HTTPS port", e);
          }
        }
      });
      this.server.setExecutor(null); // creates a default executor
    }
    catch (Exception e)
    {
      MaloWLogger.error("Exception while starting RequestListener", e);
    }
  }
}
