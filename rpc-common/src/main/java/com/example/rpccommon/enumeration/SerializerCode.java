package com.example.rpccommon.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yao 2022/12/6
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private final int code;
}
