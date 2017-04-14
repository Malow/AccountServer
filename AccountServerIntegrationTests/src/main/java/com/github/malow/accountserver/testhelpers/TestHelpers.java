package com.github.malow.accountserver.testhelpers;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

import com.github.malow.accountserver.database.AccountAccessor;
import com.github.malow.malowlib.database.DatabaseConnection;
import com.github.malow.malowlib.database.DatabaseConnection.DatabaseType;
import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;

public class TestHelpers
{
  private static AccountAccessor accountAccessor = new AccountAccessor(
      DatabaseConnection.get(DatabaseType.SQLITE_FILE, "../AccountServer/AccountServer"));

  public static void beforeTest() throws Exception
  {
    resetDatabaseTable("accounts");
    ServerConnection.clearCache();
  }

  public static boolean isValidToken(String token)
  {
    if (token != null && token.length() > 0)
    {
      return true;
    }
    return false;
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
    accountAccessor.createTable();
    /*
    String sql = "DELETE FROM " + tableName + " ;";
    try (PreparedStatement s1 = DriverManager
        .getConnection("jdbc:mysql://192.168.1.110:3306/AccountServer?" + "user=AccServUsr&password=password&autoReconnect=true")
        .prepareStatement(sql))
    {
      s1.execute();
    }
    */
  }

  private static void createDatabase() throws Exception
  {
    accountAccessor.createTable();
    /*
    try (Connection connection = DriverManager
        .getConnection("jdbc:mysql://192.168.1.110:3306?" + "user=AccServUsr&password=password&autoReconnect=true"))
    {
      runSqlStatementsFromFile(connection, "../CreateMysqlDatabase.sql");
      runSqlStatementsFromFile(connection, "../CreateSqlTables.sql");
    }
    */
  }

  private static void runSqlStatementsFromFile(Connection connection, String pathToFile) throws Exception
  {
    String file = new String(Files.readAllBytes(Paths.get(pathToFile)), StandardCharsets.UTF_8);
    String[] statements = file.split("\\;");
    for (String statement : statements)
    {
      try
      {
        connection.prepareStatement(statement + ";").execute();
      }
      catch (Exception e2)
      {
        throw e2;
      }
    }
  }

  public static String getPasswordResetTokenForEmail(String email) throws Exception
  {
    return accountAccessor.read(email).pwResetToken;
    /*
    try (PreparedStatement s1 = DriverManager
        .getConnection("jdbc:mysql://192.168.1.110:3306/AccountServer?" + "user=AccServUsr&password=password&autoReconnect=true")
        .prepareStatement("SELECT * FROM Accounts WHERE email = ? ; "))
    {
      s1.setString(1, email);
      try (ResultSet s1Res = s1.executeQuery())
      {
        if (s1Res.next())
        {
          return s1Res.getString("pw_reset_token");
        }
        return null;
      }
    }
    */
  }
}
