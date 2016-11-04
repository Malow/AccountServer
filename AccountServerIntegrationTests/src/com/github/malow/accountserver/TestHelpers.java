package com.github.malow.accountserver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.github.malow.malowlib.network.https.HttpsPostClient;
import com.google.gson.Gson;
import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;

public class TestHelpers
{
  public static void beforeTest() throws Exception
  {
    HttpsPostClient.setHost(Config.HOST);
    resetDatabaseTable("accounts");
    ServerConnection.clearCache();
  }

  public static boolean isValidToken(String token)
  {
    if (token != null && token.length() > 0) { return true; }
    return false;
  }

  public static <T> T fromJson(String json, Class<T> expectedClass)
  {
    return new Gson().fromJson(json, expectedClass);
  }

  public static void resetDatabaseTable(String tableName) throws Exception
  {
    try
    {
      doResetDatabaseTable(tableName);
    }
    catch (MySQLNonTransientConnectionException e) // Database probably doesn't exist, create it.
    {
      createDatabase();
    }
    doResetDatabaseTable(tableName);
  }

  private static void doResetDatabaseTable(String tableName) throws Exception
  {
    Class.forName("com.mysql.jdbc.Driver");
    Connection connection = DriverManager
        .getConnection("jdbc:mysql://localhost/AccountServer?" + "user=AccServUsr&password=password&autoReconnect=true");
    String sql = "DELETE FROM " + tableName + " ;";
    PreparedStatement s1 = connection.prepareStatement(sql);
    s1.execute();
  }

  private static void createDatabase() throws Exception
  {
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost?" + "user=AccServUsr&password=password&autoReconnect=true");
    String file = new String(Files.readAllBytes(Paths.get("../CreateDatabaseExample.sql")));
    String[] statements = file.split("\\;");
    for (String statement : statements)
    {
      try
      {
        connection.prepareStatement(statement + ";").execute();
      }
      catch (Exception e2)
      {

      }
    }
  }

  public static String getPasswordResetTokenForEmail(String email) throws Exception
  {
    Class.forName("com.mysql.jdbc.Driver");
    Connection connection = DriverManager
        .getConnection("jdbc:mysql://localhost/AccountServer?" + "user=AccServUsr&password=password&autoReconnect=true");
    PreparedStatement s1 = connection.prepareStatement("SELECT * FROM Accounts WHERE email = ? ; ");
    s1.setString(1, email);
    ResultSet s1Res = s1.executeQuery();

    if (s1Res.next()) { return s1Res.getString("pw_reset_token"); }
    return null;
  }
}
