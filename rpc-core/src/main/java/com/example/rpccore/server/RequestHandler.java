package com.example.rpccore.server;

import com.example.rpccommon.entity.RpcRequest;
import com.example.rpccommon.entity.RpcResponse;
import com.example.rpccommon.enumeration.ResponseCode;
import com.example.rpccore.provider.ServiceProvider;
import com.example.rpccore.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author yao 2022/12/5
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }

    /**
     * 用获取到的服务对象处理请求
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest,service);
    }

    /**
     * 通过反射，获得请求对象的方法对象，然后调用方法对象的invoke方法执行并返回调用结果
     * 可是你这里不是已经有了服务对象吗？为什么不能直接用
     * 因为不知道这个服务对象究竟是什么，也不知道有什么方法，该调用哪个方法
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
            result = method.invoke(service,rpcRequest.getParameters());
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        return result;
    }
}
