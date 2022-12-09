package com.example.rpccommon.exception;

import com.example.rpccommon.enumeration.RpcError;

/**
 * 自定义异常
 * @author yao 2022/12/5
 */
public class RpcException extends RuntimeException{

    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }
}
