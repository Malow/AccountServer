package com.github.malow.accountserver.regressiontests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.malow.accountserver.AccountServerTestFixture;
import com.github.malow.accountserver.ErrorMessages;
import com.github.malow.accountserver.ServerConnection;
import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.Response;
import com.github.malow.malowlib.GsonSingleton;

public class SendPasswordResetTokenTests extends AccountServerTestFixture
{
  @Test
  public void sendPasswordResetTokenSucessfullyTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_PASSWORD);

    String jsonResponse = ServerConnection.sendPasswordResetToken(TEST_EMAIL);
    Response response = GsonSingleton.fromJson(jsonResponse, Response.class);

    assertEquals(true, response.result);
    assertEquals(true, isValidToken(getPasswordResetTokenForEmail(TEST_EMAIL)));
  }

  @Test
  public void sendPasswordResetTokenWithUnregisteredEmailTest() throws Exception
  {
    String jsonResponse = ServerConnection.sendPasswordResetToken(TEST_EMAIL);
    ErrorResponse response = GsonSingleton.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.EMAIL_NOT_REGISTERED, response.error);
  }
}
