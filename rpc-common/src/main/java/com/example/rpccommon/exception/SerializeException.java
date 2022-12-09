package com.example.rpccommon.exception;

/**
 * 自定义序列化异常
 * 啥事没干，就给Runtime异常套了个壳
 * @author yao 2022/12/6
 */
public class SerializeException extends RuntimeException{
    public SerializeException(String msg) {
        super(msg);
    }
}
