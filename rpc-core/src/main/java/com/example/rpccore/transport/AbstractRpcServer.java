package com.example.rpccore.transport;

import com.example.rpccommon.enumeration.RpcError;
import com.example.rpccommon.exception.RpcException;
import com.example.rpccommon.util.ReflectUtil;
import com.example.rpccore.annotation.RpcService;
import com.example.rpccore.annotation.RpcServiceScan;
import com.example.rpccore.provider.ServiceProvider;
import com.example.rpccore.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @author yao 2022/12/9
 */
public abstract class AbstractRpcServer implements RpcServer{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    /**
     * 先获取启动类，检查启动类上面有没有@ServiceScan注解，有就检查有没有给值
     * 给了就扫描指定包，没有就扫描启动类所在包
     * 扫描所有@Service注解，并注册服务
     */
    public void scanServices() {
        // 获取启动类全类名
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            // 获取启动类Class对象
            startClass = Class.forName(mainClassName);
            // 判断启动类上有没有@ServiceScan注解
            if(!startClass.isAnnotationPresent(RpcServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        // 获取@ServiceScan的注解值，即服务扫描包的范围
        String basePackage = startClass.getAnnotation(RpcServiceScan.class).value();
        // 如果是空的，就给一个默认值，即启动类所在的包
        if("".equals(basePackage))
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));

        // 获取扫描路径下所有的Class
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        // 遍历所有的Class
        for(Class<?> clazz : classSet) {
            // 判断Class上面有没有@Service注解
            if(clazz.isAnnotationPresent(RpcService.class)) {
                // 如果有@Service注解，就通过反射创建该对象
                String serviceName = clazz.getAnnotation(RpcService.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                // 调用publishService方法注册服务
                if("".equals(serviceName)) {
                    // 如果@Service注解没有给值，就拿它实现的接口名程作为服务名？
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces)
                        publishService(obj, oneInterface.getCanonicalName());
                } else publishService(obj, serviceName);
            }
        }
    }

    /**
     * 现在本地添加了服务，然后向远程Nacos注册服务
     * @param service 服务对象
     * @param serviceName 服务名称
     * @param <T> 泛型服务对象
     */
    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
