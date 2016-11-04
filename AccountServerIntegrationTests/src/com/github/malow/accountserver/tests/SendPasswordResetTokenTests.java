package com.github.malow.accountserver.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.github.malow.accountserver.ErrorMessages;
import com.github.malow.accountserver.ServerConnection;
import com.github.malow.accountserver.TestHelpers;
import com.github.malow.accountserver.comstructs.ErrorResponse;
import com.github.malow.accountserver.comstructs.Response;

public class SendPasswordResetTokenTests
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
  public void sendPasswordResetTokenSucessfullyTest() throws Exception
  {
    ServerConnection.register(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);

    String jsonResponse = ServerConnection.sendPasswordResetToken(TEST_EMAIL);
    Response response = TestHelpers.fromJson(jsonResponse, Response.class);

    assertEquals(true, response.result);
    assertEquals(true, TestHelpers.isValidToken(TestHelpers.getPasswordResetTokenForEmail(TEST_EMAIL)));
  }

  @Test
  public void sendPasswordResetTokenWithUnregisteredEmailTest() throws Exception
  {
    String jsonResponse = ServerConnection.sendPasswordResetToken(TEST_EMAIL);
    ErrorResponse response = TestHelpers.fromJson(jsonResponse, ErrorResponse.class);

    assertEquals(false, response.result);
    assertEquals(ErrorMessages.EMAIL_NOT_REGISTERED, response.error);
  }
}