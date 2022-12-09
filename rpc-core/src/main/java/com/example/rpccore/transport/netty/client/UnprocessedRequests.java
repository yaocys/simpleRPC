package com.example.rpccore.transport.netty.client;

import com.example.rpccommon.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yao 2022/12/9
 */
public class UnprocessedRequests {
    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        unprocessedResponseFutures.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedResponseFutures.remove(requestId);
    }

    public void complete(RpcResponse rpcResponse) {
        // 请求完成了，就把请求从未完成的请求列表中移除
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if (null != future) {
            // 把响应对象放入future中
            future.complete(rpcResponse);
        } else throw new IllegalStateException();
    }
}
