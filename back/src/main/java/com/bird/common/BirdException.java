package com.bird.common;

/**
 * 系统内部的错误信息
 *
 * @author zhyyy
 **/
public class BirdException extends RuntimeException {
    public BirdException(String msg) {
        super(msg);
    }
}
