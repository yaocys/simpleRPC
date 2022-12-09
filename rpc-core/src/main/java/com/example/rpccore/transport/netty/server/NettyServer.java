package com.example.rpccore.transport.netty.server;

import com.example.rpccommon.enumeration.RpcError;
import com.example.rpccommon.exception.RpcException;
import com.example.rpccore.codec.CommonDecoder;
import com.example.rpccore.codec.CommonEncoder;
import com.example.rpccore.hook.ShutdownHook;
import com.example.rpccore.provider.ServiceProvider;
import com.example.rpccore.provider.ServiceProviderImpl;
import com.example.rpccore.registry.NacosServiceRegistry;
import com.example.rpccore.registry.ServiceRegistry;
import com.example.rpccore.serializer.CommonSerializer;
import com.example.rpccore.serializer.KryoSerializer;
import com.example.rpccore.transport.AbstractRpcServer;
import com.example.rpccore.transport.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

import static com.example.rpccore.transport.RpcClient.DEFAULT_SERIALIZER;

/**
 * @author yao 2022/12/6
 */
public class NettyServer extends AbstractRpcServer {

    private final CommonSerializer serializer;

    /**
     * 重载构造方法，不指定就用默认的序列化器
     */
    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }

    @Override
    public void start() {
        // 添加自动注销服务的钩子
        ShutdownHook.getShutdownHook().addClearAllHook();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 责任链模式
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new CommonEncoder(serializer))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
