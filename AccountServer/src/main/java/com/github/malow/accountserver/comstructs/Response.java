package com.github.malow.accountserver.comstructs;

import com.github.malow.malowlib.network.https.HttpResponse;

public class Response extends HttpResponse
{
  public Response(boolean result)
  {
    super(result);
  }
}
