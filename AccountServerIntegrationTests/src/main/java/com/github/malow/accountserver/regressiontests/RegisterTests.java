package com.github.malow.accountserver.regressiontests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.malow.accountserver.AccountServerTestFixture;
import com.github.malow.accountserver.ErrorMessages;
import com.github.malow.accountserver.ServerConnection;
import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.account.LoginResponse;
import com.github.malow.malowlib.GsonSingleton;

public class RegisterTests extends AccountServerTestFixture
{
  @Test
  public void registerSucessfullyTest() throws Exception
  {
    String jsonResponse = ServerConnection.register(TEST_EMAIL, TEST_PASSWORD);
    LoginResponse response = GsonSingleton.fromJson(jsonResponse, LoginResponse.class);

    assertEquals(true, response.result);
    assertEquals(true, isValidToken(response.authToken));
  }

  @Test
  public void registerAlreadyTakenEmailTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_PASSWORD);

    String jsonResponse = ServerConnection.register(TEST_EMAIL, TEST_PASSWORD);
    ErrorResponse response = GsonSingleton.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.EMAIL_TAKEN, response.error);
  }
}
