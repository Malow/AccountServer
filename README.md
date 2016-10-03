# AccountServer
A Java lib with a HTTPS-JSON-API that handles accounts.

How to use: Create an AccountServerConfig object, populate it with all config, call AccountServer.start(config). A thread will be created that runs the HTTPS-server API and your thread will return for you to do whatever with. Register, Login and ResetPW requests will all return responses that contains an authToken. This authToken should then be used to make further authorized requests which your program will handle. To check the authentication of these request simply extend or create an AuthorizedRequest object and call isValid() on it, and it will return true or false. Finally when you're done simply call AccountServer.close() to shut it all down.
