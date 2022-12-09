package com.example.rpcapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 这是简易实现中的模拟参数对象类
 * @author yao 2022/12/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable {

    private int id;

    private String message;
}
