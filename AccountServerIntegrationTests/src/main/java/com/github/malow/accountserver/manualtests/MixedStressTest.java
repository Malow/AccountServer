package com.github.malow.accountserver.manualtests;

import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.github.malow.accountserver.testhelpers.ServerConnection;
import com.github.malow.accountserver.testhelpers.TestHelpers;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.RandomNumberGenerator;
import com.github.malow.malowlib.malowprocess.MaloWProcess;

public class MixedStressTest
{
  private static final int THREAD_COUNT = 10;
  private static final int REQUESTS_PER_THREAD = 100;
  private static final String[] RANDOM_STRINGS = { "A", "B", "C", "D" };
  private static AtomicInteger progress;

  private static String getRandomString()
  {
    return RANDOM_STRINGS[RandomNumberGenerator.getRandomInt(0, RANDOM_STRINGS.length - 1)];
  }

  @Before
  public void setup() throws Exception
  {
    TestHelpers.beforeTest();
  }

  private static void doRandomRegister() throws Exception
  {
    ServerConnection.register(getRandomString(), getRandomString(), getRandomString());
  }

  private static void doRandomLogin() throws Exception
  {
    ServerConnection.login(getRandomString(), getRandomString());
  }

  private static void doRandomSendPwResetToken() throws Exception
  {
    ServerConnection.sendPasswordResetToken(getRandomString());
  }

  private static void doResetPassword() throws Exception
  {
    ServerConnection.resetPassword(getRandomString(), getRandomString(), getRandomString());
  }

  private static class Runner extends MaloWProcess
  {
    @Override
    public void life()
    {
      try
      {
        for (int i = 0; i < REQUESTS_PER_THREAD; i++)
        {
          int rand = RandomNumberGenerator.getRandomInt(0, 100);
          if (rand < 10)
          {
            doRandomRegister();
          }
          else if (rand < 90)
          {
            doRandomLogin();
          }
          else if (rand < 95)
          {
            doRandomSendPwResetToken();
          }
          else
          {
            doResetPassword();
          }
          progress.incrementAndGet();
        }
      }
      catch (Exception e)
      {
        MaloWLogger.error("Failed.", e);
        fail(e.getMessage());
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
