package yansiqi.Server;

import com.sun.xml.internal.stream.buffer.sax.Features;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.FutureListener;

/**
 * @ Author     ：yan_siqi@qq.com
 * @ Date       ：Created in 14:22 2018/12/28
 * @ Description：在超时后调用此操作
 * @ Modified By：
 * @Version: 0.1
 */
//实现 userEventTriggered() 方法以发送心跳消息
public final class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 如果检测到IdleStateEvent事件，发送心跳消息，并在发送失败时关闭该连接
        if (evt instanceof IdleStateEvent) {
            ctx.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future){
                    if(future.isSuccess()){
                        System.out.println("心跳超时，Channel已经成功关闭");
                    }else{
                        System.err.println("心跳超时，Channel关闭失败");
                        future.cause().printStackTrace();
                    }
                }
            });
        } else {
            //如果不是 IdleStateEvent 事件，所以将它传递给下一个 ChannelInboundHandler
            super.userEventTriggered(ctx, evt);
        }
    }
}