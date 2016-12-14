package com.github.malow.accountserver.regressiontests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.malow.accountserver.ErrorMessages;
import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.account.LoginResponse;
import com.github.malow.accountserver.testhelpers.ServerConnection;
import com.github.malow.accountserver.testhelpers.TestHelpers;
import com.github.malow.malowlib.GsonSingleton;

public class ResetPasswordTests
{
  private static final String TEST_PASSWORD = "testerpw";
  private static final String TEST_NEW_PASSWORD = "testerpwnew";
  private static final String TEST_USERNAME = "tester";
  private static final String TEST_EMAIL = "tester@test.com";

  @Before
  public void setup() throws Exception
  {
    TestHelpers.beforeTest();
  }

  @Test
  public void resetPasswordSucessfullyTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);
    ServerConnection.sendPasswordResetToken(TEST_EMAIL);
    String passwordResetToken = TestHelpers.getPasswordResetTokenForEmail(TEST_EMAIL);

    String jsonResponse = ServerConnection.resetPassword(TEST_EMAIL, TEST_NEW_PASSWORD, passwordResetToken);
    LoginResponse response = GsonSingleton.fromJson(jsonResponse, LoginResponse.class);

    assertEquals(true, response.result);
    assertEquals(true, TestHelpers.isValidToken(response.authToken));
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
    ServerConnection.register(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);
    String jsonResponse = ServerConnection.resetPassword(TEST_EMAIL, TEST_NEW_PASSWORD, "asd");
    ErrorResponse response = GsonSingleton.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.BAD_PW_RESET_TOKEN, response.error);
  }
}
