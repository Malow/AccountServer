package com.github.malow.accountserver.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.malow.accountserver.ErrorMessages;
import com.github.malow.accountserver.ServerConnection;
import com.github.malow.accountserver.TestHelpers;
import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.account.LoginResponse;

public class RegisterTests
{
  private static final String TEST_PASSWORD = "testerpw";
  private static final String TEST_USERNAME = "tester";
  private static final String TEST_EMAIL = "tester@test.com";

  @Before
  public void setup() throws Exception
  {
    TestHelpers.beforeTest();
  }

  @Test
  public void registerSucessfullyTest() throws Exception
  {
    String jsonResponse = ServerConnection.register(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);
    LoginResponse response = TestHelpers.fromJson(jsonResponse, LoginResponse.class);

    assertEquals(true, response.result);
    assertEquals(true, TestHelpers.isValidToken(response.authToken));
  }

  @Test
  public void registerAlreadyTakenEmailTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);

    String jsonResponse = ServerConnection.register(TEST_EMAIL, TEST_USERNAME + "a", TEST_PASSWORD);
    ErrorResponse response = TestHelpers.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.EMAIL_TAKEN, response.error);
  }

  @Test
  public void registerAlreadyTakenUsernameTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);

    String jsonResponse = ServerConnection.register(TEST_EMAIL + "a", TEST_USERNAME, TEST_PASSWORD);
    ErrorResponse response = TestHelpers.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.USERNAME_TAKEN, response.error);
  }
}