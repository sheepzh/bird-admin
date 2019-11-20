package com.bird.common.cache;


import com.bird.utils.EncryptionUtil;

/**
 * 缓存key种子,用于管理key的版本
 *
 * @author zhyyy
 **/

public class CacheKeySeed {
    /**
     * 前缀标识
     */
    private String keyPrefix;
    /**
     * 版本前缀
     */
    private String realPrefix;

    public CacheKeySeed(String prefix) {
        this.keyPrefix = prefix;
        updateVersion();
    }

    /**
     * 更新版本。更新后，之前缓存的k-v记录将（业务上）无法查询，即失效。
     * 使用该操作可完成批量(相同前缀)删除
     */
    public void updateVersion() {
        this.realPrefix = EncryptionUtil.md5(keyPrefix + System.currentTimeMillis());
    }

    public String key(Object suffix) {
        return realPrefix + suffix.toString();
    }

    public String key() {
        return realPrefix;
    }

    @Override
    public String toString() {
        return realPrefix;
    }
}

