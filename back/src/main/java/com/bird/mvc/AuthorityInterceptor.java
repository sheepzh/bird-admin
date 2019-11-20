package com.bird.mvc;

import com.bird.config.Config;
import com.bird.service.system.ITokenService;
import com.bird.utils.WebUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * token验证
 *
 * @author zhyyy
 */
@Component
public class AuthorityInterceptor implements HandlerInterceptor {

    /**
     * 登录，轮询用户信息，注销接口不进行token验证
     */
    private final static List<String> NO_TOKEN_CHECK_URI = Arrays.asList("/user/login", "/user/info", "/user/logout");

    private final Logger LOG = Logger.getLogger(AuthorityInterceptor.class);
    private final ITokenService tokenService;
    private final Config config;

    @Autowired
    public AuthorityInterceptor(ITokenService tokenService, Config config) {
        this.tokenService = tokenService;
        this.config = config;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String dev = "dev";
        if (dev.equals(config.active)) {
            return true;
        }
        String token = WebUtil.getToken(request);
        String ip = WebUtil.getIpAddress(request);
        String context = request.getRequestURI();
        if (!NO_TOKEN_CHECK_URI.contains(context)) {
            LOG.info("token校验开始:" + token);
            return tokenService.validateAndUpdate(token, ip);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}