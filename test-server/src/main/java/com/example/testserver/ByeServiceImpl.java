package com.example.testserver;

import com.example.rpcapi.ByeService;
import com.example.rpccore.annotation.RpcService;

/**
 * @author yao 2022/12/9
 */
@RpcService
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
