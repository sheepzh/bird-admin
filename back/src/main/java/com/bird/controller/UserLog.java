package com.bird.controller;

import java.lang.annotation.*;

/**
 * API接口用户操作日志注解
 *
 * @author zhyyy
 **/
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface UserLog {
    /**
     * @return 接口功能简述
     */
    String value();

    /**
     * @return 是否需要进行日志记录
     */
    boolean need() default true;

    /**
     * @return 是否无token信息
     */
    boolean noToken() default false;
}