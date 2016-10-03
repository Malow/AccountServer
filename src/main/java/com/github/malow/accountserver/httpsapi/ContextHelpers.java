package com.github.malow.accountserver.httpsapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.github.malow.accountserver.comstructs.Request;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

public class ContextHelpers
{
  public static Request getValidRequest(HttpExchange t, Class<?> c)
  {
    String msg = "";
    try
    {
      InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
      BufferedReader br = new BufferedReader(isr);
      int b;
      StringBuilder buf = new StringBuilder();
      while ((b = br.read()) != -1)
      {
        buf.append((char) b);
      }
      br.close();
      isr.close();
      msg = buf.toString();
      Request req = (Request) new Gson().fromJson(msg, c);
      if (req != null && req.isValid()) return req;
      else return null;
    }
    catch (Exception e)
    {
      System.out.println("Failed when trying to parse request: " + msg);
      return null;
    }
  }

  public static void sendMessage(HttpExchange t, int code, String response)
  {
    try
    {
      t.sendResponseHeaders(code, response.getBytes().length);
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
    catch (IOException e)
    {
      System.out.println("Failed when trying to send response: " + response);
    }
  }
}
