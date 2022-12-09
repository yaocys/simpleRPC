package com.example.rpccore.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.example.rpccommon.entity.RpcRequest;
import com.example.rpccommon.entity.RpcResponse;
import com.example.rpccommon.enumeration.SerializerCode;
import com.example.rpccommon.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author yao 2022/12/6
 */
public class KryoSerializer implements CommonSerializer{

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    /**
     * 这里看起来是在初始化并返回了配置好的kryo对象
     * 为什么这里定义的返回值的ThreadLocal？
     */
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();
            output.flush();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            logger.error("Kryo序列化时有错误发生：",e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        } catch (Exception e) {
            logger.error("反序列化时有错误发生:", e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.KRYO.getCode();
    }
}
