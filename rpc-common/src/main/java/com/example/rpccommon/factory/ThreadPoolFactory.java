package com.example.rpccommon.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author yao 2022/12/9
 */
public class ThreadPoolFactory {
    /**
     * 线程池参数
     */
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static final Map<String, ExecutorService> threadPollsMap = new ConcurrentHashMap<>();

    private ThreadPoolFactory() {
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon) {
        ExecutorService pool = threadPollsMap.computeIfAbsent(threadNamePrefix, k -> createThreadPool(threadNamePrefix, daemon));
        if (pool.isShutdown() || pool.isTerminated()) {
            threadPollsMap.remove(threadNamePrefix);
            pool = createThreadPool(threadNamePrefix, daemon);
            threadPollsMap.put(threadNamePrefix, pool);
        }
        return pool;
    }

    /**
     * 关闭所有线程池
     */
    public static void shutDownAll() {
        logger.info("关闭所有线程池...");
        // 利用parallelStream()并行关闭所有线程池
        threadPollsMap.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            logger.info("关闭线程池 [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                //阻塞直到关闭请求后所有任务执行完，或者发生超时，或者当前线程被中断（以先发生者为准）。
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ie) {
                logger.error("关闭线程池失败！");
                // 直接关闭不等线程池执行完了
                executorService.shutdownNow();
            }
        });
    }

    private static ExecutorService createThreadPool(String threadNamePrefix, Boolean daemon) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory);
    }


    /**
     * 创建 ThreadFactory 。如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用defaultThreadFactory
     *
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon           指定是否为 Daemon Thread(守护线程)
     * @return ThreadFactory
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
