package com.example.testclient;

import com.example.rpcapi.ByeService;
import com.example.rpcapi.HelloObject;
import com.example.rpcapi.HelloService;
import com.example.rpccore.serializer.CommonSerializer;
import com.example.rpccore.transport.RpcClient;
import com.example.rpccore.transport.netty.client.NettyClient;

/**
 * @author yao 2022/12/6
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        /*
        使用代理类调用hello方法
         */
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
        /*
        调用无参方法
         */
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
    }
}
