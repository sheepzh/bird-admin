package com.bird.service.system.impl;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bird.common.BirdOutException;
import com.bird.common.ResultCode;
import com.bird.common.TokenBean;
import com.bird.common.cache.CacheKeySeed;
import com.bird.common.cache.SimpleKvCache;
import com.bird.model.entity.Staff;
import com.bird.service.system.ITokenService;
import com.bird.utils.WebUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 用户token管理，使用jwt
 *
 * @author zhyyy
 */
@Component
public class TokenServiceImpl implements ITokenService {
    private final Logger LOG = Logger.getLogger(TokenServiceImpl.class);
    private final CacheKeySeed USER_TOKEN_POOL_PREFIX = new CacheKeySeed("USER_TOKEN_POOL_PREFIX");
    private final CacheKeySeed TOKEN_POOL_PREFIX = new CacheKeySeed("TOKEN_POOL_PREFIX");

    @Autowired
    private SimpleKvCache kvCache;

    @Override
    public synchronized String login(Staff user, String ip) {
        TokenBean token = generate(user, ip);
        TokenBean last = putTokenBeanByUser(user.getAccount(), token);
        // tokenTool中移除之前的记录
        if (last != null) {
            deleteToken(last.getToken());
        }
        putUserByToken(token.getToken(), user);
        return token.getToken();
    }


    @Override
    public void logout(String token) {
        Staff user = getUserByToken(token);
        if (user == null) {
            LOG.error("token所对应的用户不存在,无需登出");
            return;
        }
        deleteToken(token);
        deleteUser(user.getAccount());
        LOG.info("用户登出：" + user.getAccount() + "," + user.getName());
    }

    @Override
    public void logoutByAccount(String account) {
        TokenBean bean = getTokenBeanByUser(account);
        if (bean != null) {
            logout(bean.getToken());
        }
    }

    @Override
    public synchronized boolean validateAndUpdate(String token, String ip) {
        long now = System.currentTimeMillis();
        Staff user = getUserByToken(token);
        if (user == null) {
            return false;
        }
        // 校验token是否正确加密
        List<String> audience = JWT.decode(token).getAudience();
        boolean jwtValid = audience.size() == 2
                && Objects.equals(user.getAccount(), audience.get(0))
                && Objects.equals(ip, audience.get(1));
        if (!jwtValid) {
            LOG.info(String.format("token非法:token=%s,ip=%s", token, ip));
            return false;
        }
        TokenBean bean = getTokenBeanByUser(user.getAccount());
        if (bean == null) {
            deleteToken(token);
            return false;
        }
        long remainTime = 1000 * 60 * 10;
        if (!Objects.equals(bean.getToken(), token) || now - bean.getTs() > remainTime) {
            LOG.info("token已超时:" + token);
            deleteToken(token);
            deleteUser(user.getAccount());
            return false;
        }
        bean.setTs(now);
        LOG.info("current token bean:" + JSON.toJSONString(bean));
        putTokenBeanByUser(user.getAccount(), bean);
        putUserByToken(token, user);
        return true;
    }

    @Override
    public Staff map2User(HttpServletRequest request) {
        Staff user = getUserByToken(WebUtil.getToken(request));
        if (user != null) {
            user.setPassword(null);
        } else {
            throw new BirdOutException("登陆超时", ResultCode.LOGIN_TIMEOUT_ERROR, ResultCode.LOGIN_TIMEOUT_MSG);
        }
        return user;
    }

    private TokenBean generate(Staff user, String ip) {
        String token = JWT.create()
                .withAudience(user.getAccount(), ip)
                .sign(Algorithm.HMAC256(user.getPassword()));
        TokenBean tokenBean = new TokenBean();
        tokenBean.setToken(token);
        tokenBean.setTs(System.currentTimeMillis());
        return tokenBean;
    }

    private TokenBean getTokenBeanByUser(String userAccount) {
        return (TokenBean) kvCache.get(USER_TOKEN_POOL_PREFIX.key(userAccount));
    }

    private TokenBean putTokenBeanByUser(String userAccount, TokenBean bean) {
        return kvCache.put(USER_TOKEN_POOL_PREFIX.key(userAccount), bean);
    }

    private void deleteUser(String userAccount) {
        kvCache.delete(USER_TOKEN_POOL_PREFIX.key(userAccount));
    }

    private Staff getUserByToken(String token) {
        if (token == null) {
            return null;
        }
        Staff staff = kvCache.get(TOKEN_POOL_PREFIX.key(token));
        if (staff == null) {
            return null;
        }
        return staff;
    }

    private void putUserByToken(String token, Staff user) {
        kvCache.put(TOKEN_POOL_PREFIX.key(token), user);
    }

    private void deleteToken(String token) {
        kvCache.delete(TOKEN_POOL_PREFIX.key(token));
    }
}
