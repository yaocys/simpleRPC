package com.example.rpccore.transport;

/**
 * 服务器通用接口
 * @author yao 2022/12/6
 */
public interface RpcServer {
    /**
     * 怎么连注册表对象都不需要了？
     */
    void start();

    /**
     * 向Nacos注册服务
     * @param service 服务对象
     * @param serviceName 服务名称
     * @param <T>
     */
    <T> void publishService(T service, String serviceName);}
