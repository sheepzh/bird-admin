package com.bird.service.system;

import com.bird.model.entity.Staff;

import javax.servlet.http.HttpServletRequest;

/**
 * token服务
 *
 * @author zhyyy
 * @see com.bird.service.system.impl.TokenServiceImpl
 */
public interface ITokenService {

    /**
     * 用户登录时调用,缓存用户token,删除旧token
     * token校验用户信息以及请求IP
     *
     * @param user 系统用户信息
     * @param ip   请求IP
     * @return token
     */
    String login(Staff user, String ip);

    /**
     * 用户登出，删除token
     *
     * @param token token
     */
    void logout(String token);

    /**
     * 用户登出，删除token
     *
     * @param account 账号
     */
    void logoutByAccount(String account);


    /**
     * 验证token,并更新token过期时间
     *
     * @param token token
     * @param ip    请求IP
     * @return true 验证通过 false 验证失败并清除原token
     */
    boolean validateAndUpdate(String token, String ip);

    /**
     * 根据token获取用户
     *
     * @param request 请求信息
     * @return user
     */
    Staff map2User(HttpServletRequest request);
}
