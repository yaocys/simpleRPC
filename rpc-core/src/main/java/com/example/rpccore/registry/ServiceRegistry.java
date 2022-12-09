package com.example.rpccore.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册表，用来给服务端用，用来实现注册多个服务
 * @author yao 2022/12/5
 */
public interface ServiceRegistry {

    void register(String serviceName, InetSocketAddress inetSocketAddress);

}
