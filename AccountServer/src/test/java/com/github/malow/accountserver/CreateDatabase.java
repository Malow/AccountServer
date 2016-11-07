package com.github.malow.accountserver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;

public class CreateDatabase
{
  private static void createMysqlDatabase() throws Exception
  {
    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost?user=AccServUsr&password=password&autoReconnect=true");
    runSqlStatementsFromFile(connection, "../CreateMysqlDatabase.sql");
    runSqlStatementsFromFile(connection, "../CreateSqlTables.sql");
  }

  @SuppressWarnings("unused")
  private static void createSqlite3Database() throws Exception
  {
    Connection connection = DriverManager.getConnection("jdbc:sqlite:../AccountServer.db");
    runSqlStatementsFromFile(connection, "../CreateSqlTables.sql");
  }

  private static void runSqlStatementsFromFile(Connection connection, String pathToFile) throws Exception
  {
    String file = new String(Files.readAllBytes(Paths.get(pathToFile)));
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

  @Test
  public void createDatabaseTest() throws Exception
  {
    createMysqlDatabase();
    //createSqlite3Database();
  }
}
