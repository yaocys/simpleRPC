package com.example.rpccommon.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自定义协议二字段，包类型枚举
 * @author yao 2022/12/6
 */
@AllArgsConstructor
@Getter
public enum PackageType {
    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}
