package yansiqi;

import yansiqi.Server.EchoServer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        int port = 54147;
        EchoServer echoServer=new EchoServer(port);
        echoServer.Start();
    }
}
