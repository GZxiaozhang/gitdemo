package com.example.myseckill_demo.exception;

import com.example.myseckill_demo.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
//全局异常
public class GlobalException extends RuntimeException {
    private RespBeanEnum respBeanEnum;


}
