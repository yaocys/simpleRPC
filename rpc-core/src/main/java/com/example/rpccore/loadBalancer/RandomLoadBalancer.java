package com.example.rpccore.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @author yao 2022/12/9
 */
public class RandomLoadBalancer implements LoadBalancer{

    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }

}
