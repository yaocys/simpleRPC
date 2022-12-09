package com.example.rpccore.provider;

/**
 * @author yao 2022/12/6
 */
public interface ServiceProvider {
    /**
     * 注册一个服务到服务注册表
     */
    <T> void addServiceProvider(T service, String serviceName);

    /**
     * 根据服务名获取服务对象
     */
    Object getServiceProvider(String serviceName);
}
