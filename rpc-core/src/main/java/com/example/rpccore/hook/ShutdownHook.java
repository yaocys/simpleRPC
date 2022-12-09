package com.example.rpccore.hook;

import com.example.rpccommon.factory.ThreadPoolFactory;
import com.example.rpccommon.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yao 2022/12/9
 */
public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    /**
     * 通过Java运行时环境，添加一个新钩子（线程对象？），会在JVM关闭之前被调用
     * 被调用会清空所有的服务
     */
    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        // 通过Java运行时环境，添加一个新钩子（线程对象？），会在JVM关闭之前被调用
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
