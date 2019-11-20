package com.bird.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 自定义配置信息
 *
 * @author zhyyy
 **/
@Component
public class Config {

    @Value("${memcache.addresses:localhost:11211}")
    public String memcacheAddresses;

    @Value("${memcache.remain:17600}")
    public int memcacheRemain = 20 * 24 * 3600;

    @Value("${spring.profiles.active:pro}")
    public String active;
}
