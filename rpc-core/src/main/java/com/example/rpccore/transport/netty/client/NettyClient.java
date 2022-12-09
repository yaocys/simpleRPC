package com.example.rpccore.transport.netty.client;

import com.example.rpccommon.entity.RpcRequest;
import com.example.rpccommon.entity.RpcResponse;
import com.example.rpccommon.enumeration.RpcError;
import com.example.rpccommon.exception.RpcException;
import com.example.rpccommon.factory.SingletonFactory;
import com.example.rpccore.codec.CommonDecoder;
import com.example.rpccore.codec.CommonEncoder;
import com.example.rpccore.loadBalancer.LoadBalancer;
import com.example.rpccore.loadBalancer.RandomLoadBalancer;
import com.example.rpccore.registry.NacosServiceDiscovery;
import com.example.rpccore.registry.NacosServiceRegistry;
import com.example.rpccore.registry.ServiceDiscovery;
import com.example.rpccore.registry.ServiceRegistry;
import com.example.rpccore.serializer.CommonSerializer;
import com.example.rpccore.serializer.KryoSerializer;
import com.example.rpccore.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yao 2022/12/6
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;

    // 配置Netty客户端
    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }
    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }
    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }
    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    /**
     * 从Nacos获取服务并发送请求
     * @param rpcRequest 请求对象
     * @return
     */
    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            // 从远程Nacos获取服务地址
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            // 构造Channel
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
