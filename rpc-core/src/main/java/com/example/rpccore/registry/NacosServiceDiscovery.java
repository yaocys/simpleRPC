package com.example.rpccore.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.example.rpccommon.enumeration.RpcError;
import com.example.rpccommon.exception.RpcException;
import com.example.rpccommon.util.NacosUtil;
import com.example.rpccore.loadBalancer.LoadBalancer;
import com.example.rpccore.loadBalancer.RandomLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author yao 2022/12/9
 */
public class NacosServiceDiscovery implements ServiceDiscovery{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if(loadBalancer == null) this.loadBalancer = new RandomLoadBalancer();
        else this.loadBalancer = loadBalancer;
    }

    /**
     * 调用工具类获得所有的服务实例列表，并根据负载均衡策略类的select方法从中选出一个
     * @param serviceName 服务名称
     * @return 返回服务ip和端口封装的InetSocketAddress对象
     */
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            if(instances.size() == 0) {
                logger.error("找不到对应的服务: " + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
        }
        return null;
    }
}
