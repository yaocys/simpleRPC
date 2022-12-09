package com.example.rpccore.provider;

import com.example.rpccommon.enumeration.RpcError;
import com.example.rpccommon.exception.RpcException;
import com.example.rpccore.registry.NacosServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yao 2022/12/6
 */
public class ServiceProviderImpl implements ServiceProvider{


    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    /*
    注意，为什么这里不用同一个，这里两个容器中存的的key不是同一个东西
    set中存的是服务类对象的全路径名，map中存的服务类对象实现的所有接口的全路径名
     */

    /**
     * 用来保存服务名与实例的映射，而且是多对多映射
     */
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    /**
     * 用来保存所有已经注册了的服务名
     */
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    /**
     * 注意这个synchronized
     * 为什么要一个泛型返回值？这里也没用到啊，也没返回什么啊
     */
    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    /**
     * 根据服务名获取服务对象实例，去map里面拿…map里面不是接口名吗？也不是服务对象名啊
     */
    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service==null) throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        return service;
    }
}
