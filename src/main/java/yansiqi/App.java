package yansiqi;
import yansiqi.EchoServer.EchoServer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        //设置端口值（如果端口参数的格式不正确，则抛出一个NumberFormatException）
        int port = 54137;
        //调用服务器的 start()方法
        new EchoServer(port).start();
    }
}
