package com.example.rpccore.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
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
public class HessianSerializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

    @Override
    public byte[] serialize(Object obj) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    logger.error("关闭流时有错误发生:", e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            hessianInput = new HessianInput(byteArrayInputStream);
            return hessianInput.readObject();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        } finally {
            if (hessianInput != null) hessianInput.close();
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("HESSIAN").getCode();
    }
}
