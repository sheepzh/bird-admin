package com.bird.common;

/**
 * 状态码集合
 *
 * @author zhyyy
 **/
public interface ResultCode {
    int RESPONSE_SUCCESS = 1;

    int RESPONSE_FAIL = 0;
    String INIT_ERROR_MSG = "未知错误";

    int INTERNAL_ERROR = -1;
    String INTERNAL_ERROR_MSG = "系统内部错误";

    // 参数错误

    int PARAM_ERROR = 10001;
    String PARAM_ERROR_MSG = "参数错误";

    // 业务逻辑错误

    int USER_NOT_FOUND = 30001;
    String USER_NOT_FOUND_MSG = "未找到该用户";
    int USER_FORBIDDEN = 30002;
    String USER_FORBIDDEN_MSG = "用户已被禁用";
    int PASSWORD_ERROR = 30003;
    String PASSWORD_ERROR_MSG = "密码错误";
    int LOGIN_TIMEOUT_ERROR = 30005;
    String LOGIN_TIMEOUT_MSG = "登录超时，请重新登录";

}
