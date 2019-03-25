package com.github.malow.accountserver.comstructs;

import com.github.malow.malowlib.network.https.JsonHttpResponse;

public class Response extends JsonHttpResponse
{
  public Response(boolean result)
  {
    super(result);
  }
}
