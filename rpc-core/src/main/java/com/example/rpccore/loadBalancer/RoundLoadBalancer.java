package com.example.rpccore.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author yao 2022/12/9
 */
public class RoundLoadBalancer implements LoadBalancer{
    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if(index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}
