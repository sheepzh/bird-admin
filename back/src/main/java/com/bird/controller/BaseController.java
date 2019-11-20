package com.bird.controller;

import com.bird.common.BirdException;
import com.bird.common.BirdOutException;
import com.bird.common.Response;
import com.bird.common.ResultCode;
import com.bird.utils.StringUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * @author zhyyy
 */
public class BaseController implements ResultCode {
    private static final Logger LOG = Logger.getLogger(BaseController.class);

    /**
     * response封装
     *
     * @param function 业务逻辑处理
     * @return 返回数据
     */
    protected Map<String, Object> responseWrap(Consumer<Response> function) {
        Response response = new Response();
        try {
            function.accept(response);
        } catch (Exception e) {
            e.printStackTrace();
            errorDeal(response, e);
        }
        return response.map();
    }

    /**
     * response封装
     *
     * @param function 业务逻辑处理
     * @return 返回数据
     */
    protected Map<String, Object> responseWrap(Supplier<Object> function) {
        Response response = new Response();
        try {
            response.setResult(function.get());
        } catch (Exception e) {
            e.printStackTrace();
            errorDeal(response, e);
        }
        return response.map();
    }

    private void errorDeal(Response response, Exception e) {
        if (e instanceof BirdOutException) {
            BirdOutException e1 = (BirdOutException) e;
            response.fail(e1.getErrorCode(), e1.getErrorMsg());
        } else if (e instanceof SQLException) {
            response.fail(-2, "数据库错误");
        } else {
            response.fail(INTERNAL_ERROR, INTERNAL_ERROR_MSG);
        }
        LOG.error(e);
    }

    protected void assertEmpty(Object val, int failCode, String msg) {
        boolean isEmpty = (val instanceof String && StringUtil.isBlank((String) val)) || val == null;
        if (isEmpty) {
            throw new BirdOutException(msg, failCode, msg);
        }
    }

    protected void assertEmpty(Object val, String msg) {
        boolean isEmpty = (val instanceof String && StringUtil.isBlank((String) val)) || val == null;
        if (isEmpty) {
            throw new BirdException(msg);
        }
    }

    protected void assertNull(Object val, int failCode, String msg) {
        if (val == null) {
            throw new BirdOutException(msg, failCode, msg);
        }
    }

    protected void assertNull(Object val, String msg) {
        if (val == null) {
            throw new BirdException(msg);
        }
    }
}
