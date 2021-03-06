package com.github.malow.accountserver.regressiontests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.malow.accountserver.AccountServerTestFixture;
import com.github.malow.accountserver.ErrorMessages;
import com.github.malow.accountserver.ServerConnection;
import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.account.LoginResponse;
import com.github.malow.malowlib.GsonSingleton;

public class LoginTests extends AccountServerTestFixture
{
  @Test
  public void loginSucessfullyTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_PASSWORD);

    String jsonResponse = ServerConnection.login(TEST_EMAIL, TEST_PASSWORD);
    LoginResponse response = GsonSingleton.fromJson(jsonResponse, LoginResponse.class);

    assertEquals(true, response.result);
    assertEquals(true, isValidToken(response.authToken));
  }

  @Test
  public void loginWithWrongPasswordTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_PASSWORD);

    String jsonResponse = ServerConnection.login(TEST_EMAIL, TEST_PASSWORD + "a");
    ErrorResponse response = GsonSingleton.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.WRONG_PASSWORD, response.error);
  }

  @Test
  public void loginWithUnregisteredEmailTest() throws Exception
  {
    String jsonResponse = ServerConnection.login(TEST_EMAIL, TEST_PASSWORD);
    ErrorResponse response = GsonSingleton.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.EMAIL_NOT_REGISTERED, response.error);
  }
}
