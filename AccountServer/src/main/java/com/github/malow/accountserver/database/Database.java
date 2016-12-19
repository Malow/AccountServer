package com.github.malow.accountserver.database;

import java.sql.Connection;
import java.sql.DriverManager;

import com.github.malow.malowlib.MaloWLogger;

public class Database
{
  public static class UnexpectedException extends Exception
  {
    private static final long serialVersionUID = -3779400709345132187L;

    public UnexpectedException(String error, Exception e)
    {
      super(error, e);
    }

    public UnexpectedException(String error)
    {
      super(error);
    }
  }

  private static Connection connection = null;

  private Database()
  {
  }

  public static Connection getConnection()
  {
    return connection;
  }

  public static void init(String databaseName, String databaseUser, String databasePassword)
  {
    try
    {
      connection = DriverManager.getConnection(
          "jdbc:mysql://localhost/" + databaseName + "?user=" + databaseUser + "&password=" + databasePassword + "&autoReconnect=true");
    }
    catch (Exception e)
    {
      MaloWLogger.error("Error while trying to start SQL connection", e);
    }
  }

  public static void close()
  {
    if (connection != null)
    {
      try
      {
        connection.close();
      }
      catch (Exception e)
      {
        MaloWLogger.error("Error while trying to close SQL connection", e);
      }
      connection = null;
    }
  }
}
