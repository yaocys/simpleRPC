package com.example.rpccore.codec;

import com.example.rpccommon.entity.RpcRequest;
import com.example.rpccommon.entity.RpcResponse;
import com.example.rpccommon.enumeration.PackageType;
import com.example.rpccommon.enumeration.RpcError;
import com.example.rpccommon.exception.RpcException;
import com.example.rpccore.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author yao 2022/12/6
 */
public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        /*
        校验协议（魔数）
         */
        int magicNum = byteBuf.readInt();
        if(magicNum!=MAGIC_NUMBER){
            logger.error("不识别的协议包: {}", magicNum);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageCode = byteBuf.readInt();
        /*
        校验包类型
         */
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        /*
        获取序列化器
         */
        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        /*
        根据头字段数据长度，获取字节数组
         */
        int dataLength = byteBuf.readInt();
        byte[] bytes = new byte[dataLength];
        byteBuf.readBytes(bytes);
        // 将字节数组反序列化为对象
        Object obj = serializer.deserialize(bytes, packageClass);

        list.add(obj);
    }
}
