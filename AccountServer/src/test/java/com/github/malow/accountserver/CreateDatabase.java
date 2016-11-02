package com.github.malow.accountserver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;

public class CreateDatabase
{
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

  @Test
  public void createDatabaseTest() throws Exception
  {
    createDatabase();
  }
}
