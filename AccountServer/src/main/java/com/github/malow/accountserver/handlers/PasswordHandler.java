package com.github.malow.accountserver.handlers;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHandler
{
  private static int workload = 8;

  public static String hashPassword(String password_plaintext)
  {
    return BCrypt.hashpw(password_plaintext, BCrypt.gensalt(workload));
  }

  public static boolean checkPassword(String password_plaintext, String password_encrypted)
  {
    if (password_encrypted == null || !password_encrypted.startsWith("$2a$"))
    {
      throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
    }
    return BCrypt.checkpw(password_plaintext, password_encrypted);
  }
}