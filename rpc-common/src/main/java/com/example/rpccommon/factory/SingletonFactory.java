package com.example.rpccommon.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yao 2022/12/9
 */
public class SingletonFactory {
    private static final Map<Class<?>, Object> objectMap = new HashMap<>();

    private SingletonFactory() {}

    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objectMap.get(clazz);
        synchronized (clazz) {
            /*
             * 从map中获取不到就代表单例不存在，那就创建一个放进去
             */
            if(instance == null) {
                try {
                    instance = clazz.newInstance();
                    objectMap.put(clazz, instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        // 强制类型转换，将obj转化成T类型
        return clazz.cast(instance);
    }
}
