package com.example.testserver;

import com.example.rpcapi.HelloService;
import com.example.rpccore.annotation.RpcServiceScan;
import com.example.rpccore.provider.ServiceProvider;
import com.example.rpccore.provider.ServiceProviderImpl;
import com.example.rpccore.registry.NacosServiceRegistry;
import com.example.rpccore.registry.ServiceRegistry;
import com.example.rpccore.serializer.CommonSerializer;
import com.example.rpccore.transport.RpcServer;
import com.example.rpccore.transport.netty.server.NettyServer;

/**
 * @author yao 2022/12/6
 */
@RpcServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        // HelloService helloService = new HelloServiceImpl();
/*        ServiceRegistry registry = new NacosServiceRegistry();
        registry.register(helloService);*/
/*        ServiceProvider serviceProvider = new ServiceProviderImpl();
        serviceProvider.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);*/
        RpcServer server = new NettyServer("127.0.0.1", 9991, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }
}
