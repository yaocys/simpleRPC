package com.example.rpccore.transport;

import com.example.rpccommon.entity.RpcRequest;
import com.example.rpccore.serializer.CommonSerializer;

/**
 * 通用服务器接口
 * @author yao 2022/12/6
 */
public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    /**
     * 发送请求
     * @param request 请求对象
     * @return 相应对象
     */
    Object sendRequest(RpcRequest request);
}
