package com.github.malow.accountserver.manualtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.github.malow.accountserver.comstructs.account.LoginResponse;
import com.github.malow.accountserver.testhelpers.ServerConnection;
import com.github.malow.accountserver.testhelpers.TestHelpers;
import com.github.malow.malowlib.GsonSingleton;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.malowprocess.MaloWProcess;

public class LoginStressTest
{
  private static final int THREAD_COUNT = 10;
  private static final int REQUESTS_PER_THREAD = 100;
  private static final String TEST_PASSWORD = "testerpw";
  private static final String TEST_USERNAME = "tester";
  private static final String TEST_EMAIL = "tester@test.com";

  private static AtomicInteger progress;

  @Before
  public void setup() throws Exception
  {
    TestHelpers.beforeTest();
  }

  private static class Runner extends MaloWProcess
  {
    @Override
    public void life()
    {
      for (int i = 0; i < REQUESTS_PER_THREAD; i++)
      {
        try
        {
          String jsonResponse = ServerConnection.login(TEST_EMAIL, TEST_PASSWORD);
          LoginResponse response = GsonSingleton.fromJson(jsonResponse, LoginResponse.class);

          assertThat(response.result).isTrue();
          assertThat(TestHelpers.isValidToken(response.authToken)).isTrue();
          progress.incrementAndGet();
        }
        catch (Exception e)
        {
          MaloWLogger.error("Failed.", e);
          fail(e.getMessage());
        }
      }
    }

    @Override
    public void closeSpecific()
    {
    }
  }

  @Test
  public void stressTest() throws Exception
  {
    DecimalFormat df = new DecimalFormat("#.##");
    progress = new AtomicInteger(0);
    ServerConnection.register(TEST_EMAIL, TEST_USERNAME, TEST_PASSWORD);
    List<Runner> runners = new ArrayList<Runner>();
    for (int i = 0; i < THREAD_COUNT; i++)
    {
      runners.add(new Runner());
    }
    for (int i = 0; i < THREAD_COUNT; i++)
    {
      runners.get(i).start();
    }
    boolean done = false;
    while (!done)
    {
      done = true;
      for (int i = 0; i < THREAD_COUNT; i++)
      {
        if (!runners.get(i).getState().equals(MaloWProcess.ProcessState.FINISHED))
        {
          done = false;
        }
      }
      System.out.println("Progress: " + df.format(progress.doubleValue() / (THREAD_COUNT * REQUESTS_PER_THREAD) * 100) + "%");
    }
  }
}
