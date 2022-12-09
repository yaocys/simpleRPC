package com.example.rpccore.serializer;

/**
 * @author yao 2022/12/6
 */
public interface CommonSerializer {
    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    /**
     * 根据不同的枚举代号返回序列化器对象
     * @param code 序列化器的枚举代号
     * @return 序列化器（父类接口指向子类实现）
     */
    static CommonSerializer getByCode(int code) {
        // TODO 这里能否做到单例实现
        return switch (code) {
            case 0 -> new KryoSerializer();
            case 1 -> new JsonSerializer();
            case 2 -> new HessianSerializer();
            case 3 -> new ProtobufSerializer();
            default -> null;
        };
    }

    /**
     * 序列化，将指定对象序列化为byte数组
     * @param obj 需要序列化的对象
     * @return 被序列化为byte数组的对象
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化，将字节数组反序列化为指定对象
     */
    Object deserialize(byte[] bytes, Class<?> clazz);

    /**
     * 返回序列化器的代码
     */
    int getCode();

}
