package com.example.myseckill_demo.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  注解：接口流的限制，类似于filter或者interceptor拦截器，如果再次需要这样的流的限制的话，只需要添加注解即可。
 */
@Retention(RetentionPolicy.RUNTIME) //运行时生效
@Target(ElementType.METHOD) //方法上的注解
public @interface AccessLimit {

    int second();

    int maxCount();

    boolean needLogin() default true;
}
