package com.example.testclient;

import com.example.rpcapi.HelloObject;
import com.example.rpcapi.HelloService;
import com.example.rpccore.transport.RpcClient;

/**
 * 测试客户端发送调用请求
 * @author yao 2022/12/5
 */
@Deprecated
public class TestClient {
    public static void main(String[] args) {
/*        // 实例化代理对象并设置IP和端口的属性值
        RpcClientProxy proxy = new Client("127.0.0.1",9000);
        *//*
        因为本地并没有实现类，所以通过JDK的动态代理实例化了一个代理对象（被指向为指定类型对象）
        被代理对象调用方法时，就会执行代理对象的invoke方法
        Java内置的invoke方法能获取调用的方法对象（可以获取接口名、方法名、参数类型列表，但是传入的是接口对象没有实现所以不能执行）和传入的方法参数
        但是这样就足够了，只要能指明方法信息，执行就通过网络IO交给服务端执行，获取返回值就可以了
         *//*
        HelloService helloService = proxy.getProxy(HelloService.class);
        // 准备参数并调用方法
        HelloObject object = new HelloObject(12,"send a message");
        // 这里其实就相当于是一个远程代理
        String res = helloService.hello(object);
        System.out.println("接收到响应数据："+res);*/
    }
}
