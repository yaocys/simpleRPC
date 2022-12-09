package com.example.rpcapi;

/**
 * 方法调用的接口类，但是只有服务端有实现
 *
 * @author yao 2022/12/5
 */
public interface HelloService {
    String hello(HelloObject helloObject);
}
