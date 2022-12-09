package com.example.rpccommon.util;

import com.example.rpccommon.entity.RpcRequest;
import com.example.rpccommon.entity.RpcResponse;
import com.example.rpccommon.enumeration.ResponseCode;
import com.example.rpccommon.enumeration.RpcError;
import com.example.rpccommon.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检查请求与相应ID
 * @author yao 2022/12/7
 */
public class RpcMessageChecker {
    public static final String INTERFACE_NAME = "interfaceName";
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    private RpcMessageChecker() {
    }

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse) {
        if (rpcResponse == null) {
            logger.error("调用服务失败,serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        // 响应与请求的请求号不同
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        // 调用失败
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
