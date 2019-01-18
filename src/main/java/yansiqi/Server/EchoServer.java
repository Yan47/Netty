package yansiqi.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ Author     ：yan_siqi@qq.com
 * @ Date       ：Created in 9:36 2018/12/28
 * @ Description：回显服务器的引导类
 * @ Modified By：
 * @Version: 0.1
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void Start() throws Exception{
        // 创建EventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 设置引导参数
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 指定所使用的 NIO 传输 Channel
                    .childHandler(new ChannelInitializerImpl());

            // ServerChannel绑定端口
            ChannelFuture future = bootstrap.bind("127.0.0.1",port);

            // 异步处理绑定操作完成后的流程
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println(EchoServer.class.getName() +
                                " started and listening for connections on " + future.channel().localAddress());
//                        System.out.println("Server bound");
                    }else{
                        System.err.println("Bound attempt failed");
                        future.cause().printStackTrace();
                    }
                }
            });
            //(7) 获取 Channel 的CloseFuture，并且阻塞当前线程(即为main所在的线程)直到它完成
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }

    // 加载多个channelHandler
    final class ChannelInitializerImpl extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            // 加入解码器
//            pipeline.addLast(new LineBasedFrameDecoder(64*1024));
            final ByteBuf delimiterSpace= Unpooled.buffer();
            delimiterSpace.writeBytes(" ".getBytes());
            pipeline.addLast(new DelimiterBasedFrameDecoder(64000,delimiterSpace));

            // 加入默认超时触犯，此项Handler应该在pipeline的前端
            pipeline.addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
            // 加入业务逻辑
            pipeline.addLast(new EchoServerHandler());
            // 加入超时响应
            pipeline.addLast(new HeartbeatHandler());
        }

    }

}
