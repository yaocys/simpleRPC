package com.example.rpccommon.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.naming.directory.SearchResult;
import java.io.Serializable;

/**
 * 方法调用的请求对象
 * @author yao 2022/12/5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    private String requestId;
    /**
     * 需要调用方法所属的接口名
     */
    private String interfaceName;
    /**
     * 需要调用方法的方法名
     */
    private String methodName;
    /**
     * 参数对象列表
     */
    private Object[] parameters;
    /**
     * 参数类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;
}
