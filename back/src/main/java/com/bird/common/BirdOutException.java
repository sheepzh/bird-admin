package com.bird.common;

/**
 * 向前端传递的异常信息
 *
 * @author zhyyy
 **/
public class BirdOutException extends RuntimeException {
    private int errorCode;
    private String errorMsg;

    public BirdOutException(String msg) {
        this(msg, 0, msg);
    }

    public BirdOutException(String message, int errorCode, String errorMsg) {
        super(message);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BirdOutException(String message, Throwable cause, int errorCode, String errorMsg) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BirdOutException(Throwable cause, int errorCode, String errorMsg) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BirdOutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int errorCode, String errorMsg) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public BirdOutException setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public BirdOutException setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }
}
