package com.example.rpccommon.entity;

import com.example.rpccommon.enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 方法调用的响应类
 * @author yao 2022/12/5
 */
@Data
public class RpcResponse<T> implements Serializable {

    private String requestId;
    /**
     * 返回的响应状态码
     */
    private Integer statusCode;
    /**
     * 返回的响应消息
     */
    private String message;
    /**
     * 泛型，返回的响应数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data,String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(ResponseCode code,String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
