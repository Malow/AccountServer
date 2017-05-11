package com.github.malow.accountserver.comstructs;

import com.github.malow.malowlib.network.https.HttpsPostResponse;

public class Response extends HttpsPostResponse
{
  public Response(boolean result)
  {
    super(result);
  }
}
