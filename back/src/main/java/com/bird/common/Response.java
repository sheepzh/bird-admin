package com.bird.common;


import com.bird.utils.StringUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 返回信息封装
 *
 * @author zhyyy
 * @see com.bird.controller.BaseController
 */
public class Response implements Serializable, ResultCode {

    public final static String RESULT_CODE = "code";
    public final static String RESULT_MSG = "msg";

    private Map<String, Object> data = new HashMap<>();

    public Response() {
        success();
    }

    public Response setResult(Object result) {
        if (get("result") != null) {
            throw new BirdOutException("Duplicate result set");
        }
        set("result", result);
        return this;
    }

    public Response set(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public Response addAll(Map<String, ?> toAdd) {
        data.putAll(toAdd);
        return this;
    }

    public Response remove(String key) {
        data.remove(key);
        return this;
    }

    public Response clear() {
        data.clear();
        return this;
    }

    public Response success(String msg) {
        set(RESULT_CODE, RESPONSE_SUCCESS);
        if (StringUtil.isBlank(msg)) {
            remove(RESULT_MSG);
        } else {
            set(RESULT_MSG, msg);
        }
        return this;
    }

    public Response success() {
        return success(StringUtil.EMPTY_STRING);
    }

    public Response fail(int resultCode, String msg) {
        return clear().set(RESULT_CODE, resultCode).set(RESULT_MSG, msg);
    }

    public Response fail(String msg) {
        return fail(-1, msg);
    }

    public boolean isSuccess() {
        return (int) data.get(RESULT_CODE) == RESPONSE_SUCCESS;
    }

    public Response setSuccess(boolean success) {
        return this;
    }

    public Object get(String key) {
        return data.get(key);
    }

    /**
     * 返回结果如下
     * {
     * code:
     * msg:
     * result:
     * xx:
     * }
     *
     * @return 返回体map
     * @see com.bird.controller.BaseController#responseWrap(Supplier)
     * @see com.bird.controller.BaseController#responseWrap(Consumer)
     */
    public Map<String, Object> map() {
        return data;
    }

    public String getMessage() {
        return (String) this.get(RESULT_MSG);
    }
}
