package com.bird.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * web相关工具
 *
 * @author zhyyy
 **/
public class WebUtil {
    /**
     * 获取IP地址信息
     *
     * @param request 请求
     * @return ip信息
     */
    public static String getIpAddress(HttpServletRequest request) {
        Objects.requireNonNull(request);
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取请求UA
     *
     * @param request 请求
     * @return User-Agent
     */
    public static String getUserAgent(HttpServletRequest request) {
        Objects.requireNonNull(request);
        return request.getHeader("User-Agent");
    }

    /**
     * 获取请求token
     *
     * @param request 请求
     * @return token
     */
    public static String getToken(HttpServletRequest request) {
        Objects.requireNonNull(request);
        return request.getHeader("B-Token");
    }
}
