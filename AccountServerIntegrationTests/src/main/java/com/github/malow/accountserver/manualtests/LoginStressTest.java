package com.github.malow.accountserver.manualtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.github.malow.accountserver.AccountServerTestFixture;
import com.github.malow.accountserver.ServerConnection;
import com.github.malow.accountserver.comstructs.account.LoginResponse;
import com.github.malow.malowlib.GsonSingleton;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.malowprocess.MaloWProcess;

public class LoginStressTest extends AccountServerTestFixture
{
  private static final int THREAD_COUNT = 10;
  private static final int REQUESTS_PER_THREAD = 50;

  private static AtomicInteger progress;

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
          assertThat(isValidToken(response.authToken)).isTrue();
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
    ServerConnection.register(TEST_EMAIL, TEST_PASSWORD);
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
