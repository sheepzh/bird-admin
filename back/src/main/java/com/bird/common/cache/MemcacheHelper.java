package com.bird.common.cache;

import com.bird.common.BirdOutException;
import com.bird.config.Config;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * xmemcached再封装，整合memcached配置
 *
 * @author zhyyy
 **/
@Component
public class MemcacheHelper implements SimpleKvCache {

    private final Logger LOG = Logger.getLogger(MemcacheHelper.class);

    private final Config config;

    private MemcachedClient client;

    @Autowired
    public MemcacheHelper(Config config) {
        this.config = config;
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(config.memcacheAddresses));
        try {
            client = builder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T put(String key, T val) {
        try {
            T last = client.get(key);
            client.set(key, config.memcacheRemain, val);
            return last;
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            e.printStackTrace();
            throw new BirdOutException("缓存失败:" + key + "=" + val);
        }
    }

    @Override
    public <T> T get(String key) {
        try {
            return client.get(key);
        } catch (InterruptedException | MemcachedException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(String key) {
        try {
            return client.delete(key);
        } catch (InterruptedException | MemcachedException | TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public <T> T getOrDefault(String key, Supplier<T> supplier) {
        T res = get(key);
        if (res == null) {
            res = supplier.get();
            if (res != null) {
                put(key, res);
            } else {
                LOG.warn("getOrDefault:Produced null result");
            }
        }
        return res;
    }
}