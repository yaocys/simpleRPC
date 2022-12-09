package com.example.rpccore.transport.netty.server;

import com.example.rpccommon.entity.RpcRequest;
import com.example.rpccommon.entity.RpcResponse;
import com.example.rpccommon.factory.SingletonFactory;
import com.example.rpccore.registry.NacosServiceRegistry;
import com.example.rpccore.registry.ServiceRegistry;
import com.example.rpccore.server.RequestHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty服务端负责具体处理请求的处理器
 * @author yao 2022/12/6
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private final RequestHandler requestHandler;

    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
        try {
            if (msg.getHeartBeat()) {
                logger.info("接收到客户端心跳包...");
                return;
            }
            logger.info("服务器接收到请求: {}", msg);
            Object result = requestHandler.handle(msg);
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
            } else
                logger.error("通道不可写");

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 自定义超时逻辑处理
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else super.userEventTriggered(ctx, evt);
    }
}
