package com.example.testserver;

import com.example.rpcapi.HelloObject;
import com.example.rpcapi.HelloService;
import com.example.rpccore.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yao 2022/12/5
 */
@RpcService
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject helloObject) {
        logger.info("接收到参数：{}",helloObject.getMessage());
        return "远程调用的返回值，参数对象的ID是"+helloObject.getId();
    }
}
