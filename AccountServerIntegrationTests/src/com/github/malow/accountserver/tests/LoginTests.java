package com.github.malow.accountserver.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.malow.accountserver.ErrorMessages;
import com.github.malow.accountserver.ServerConnection;
import com.github.malow.accountserver.TestHelpers;
import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.account.LoginResponse;

public class LoginTests
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
  public void loginSucessfullyTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);

    String jsonResponse = ServerConnection.login(TEST_EMAIL, TEST_PASSWORD);
    LoginResponse response = TestHelpers.fromJson(jsonResponse, LoginResponse.class);

    assertEquals(true, response.result);
    assertEquals(true, TestHelpers.isValidToken(response.authToken));
  }

  @Test
  public void loginWithWrongPasswordTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);

    String jsonResponse = ServerConnection.login(TEST_EMAIL, TEST_PASSWORD + "a");
    ErrorResponse response = TestHelpers.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.WRONG_PASSWORD, response.error);
  }

  @Test
  public void loginWithUnregisteredEmailTest() throws Exception
  {
    String jsonResponse = ServerConnection.login(TEST_EMAIL, TEST_PASSWORD);
    ErrorResponse response = TestHelpers.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.EMAIL_NOT_REGISTERED, response.error);
  }
}
