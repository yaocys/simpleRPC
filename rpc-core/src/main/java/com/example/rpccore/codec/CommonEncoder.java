package com.example.rpccore.codec;

import com.example.rpccommon.entity.RpcRequest;
import com.example.rpccommon.enumeration.PackageType;
import com.example.rpccore.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 通用编码器
 * @author yao 2022/12/6
 */
public class CommonEncoder extends MessageToByteEncoder {

    /**
     * 魔数，用来标识协议
     */
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    /**
     * 指定序列化器
     */
    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer){
        this.serializer = serializer;
    }

    /**
     * 编码过程，其实就是依次把四个头字段和字节数组写入到缓冲中
     * @param o 待序列化的对象
     * @param byteBuf 字节缓冲区
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) {
        byteBuf.writeInt(MAGIC_NUMBER);
        if (o instanceof RpcRequest)
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        else
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());

        byteBuf.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(o);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
