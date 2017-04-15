package com.github.malow.accountserver.regressiontests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.malow.accountserver.AccountServerTestFixture;
import com.github.malow.accountserver.ErrorMessages;
import com.github.malow.accountserver.ServerConnection;
import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.account.LoginResponse;
import com.github.malow.malowlib.GsonSingleton;

public class ResetPasswordTests extends AccountServerTestFixture
{
  @Test
  public void resetPasswordSucessfullyTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_PASSWORD);
    ServerConnection.sendPasswordResetToken(TEST_EMAIL);
    String passwordResetToken = getPasswordResetTokenForEmail(TEST_EMAIL);

    String jsonResponse = ServerConnection.resetPassword(TEST_EMAIL, TEST_NEW_PASSWORD, passwordResetToken);
    LoginResponse response = GsonSingleton.fromJson(jsonResponse, LoginResponse.class);

    assertEquals(true, response.result);
    assertEquals(true, isValidToken(response.authToken));
  }

  @Test
  public void resetPasswordWithUnregisteredEmailTest() throws Exception
  {
    String jsonResponse = ServerConnection.resetPassword(TEST_EMAIL, TEST_NEW_PASSWORD, "asd");
    ErrorResponse response = GsonSingleton.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.EMAIL_NOT_REGISTERED, response.error);
  }

  @Test
  public void resetPasswordWithBadTokenTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_PASSWORD);
    String jsonResponse = ServerConnection.resetPassword(TEST_EMAIL, TEST_NEW_PASSWORD, "asd");
    ErrorResponse response = GsonSingleton.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.BAD_PW_RESET_TOKEN, response.error);
  }
}
