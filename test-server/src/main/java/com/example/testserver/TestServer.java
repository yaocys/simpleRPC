package com.example.testserver;

/**
 * @author yao 2022/12/5
 */
public class TestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
/*        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService,9000);*/
/*        ServiceRegistry serviceRegistry = new NacosServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);*/
    }
}
