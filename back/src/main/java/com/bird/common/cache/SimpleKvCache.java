package com.bird.common.cache;

import com.bird.common.BirdOutException;

import java.util.function.Supplier;

/**
 * 简单K-V缓存接口
 *
 * @author zhy
 */
public interface SimpleKvCache {
    /**
     * 获取value，没有则创建一个并存入
     *
     * @param key      key
     * @param supplier 如何生产默认value
     * @param <T>      value类型
     * @return value
     */
    <T> T getOrDefault(String key, Supplier<T> supplier);

    /**
     * 获取数据，没有则返回null
     *
     * @param key key
     * @param <T> value类型
     * @return value
     */
    <T> T get(String key);

    /**
     * 更新value
     *
     * @param key key
     * @param val value
     * @param <T> value类型
     * @return 之前的值，没有则为null
     * @throws BirdOutException 缓存失败时抛出
     */
    <T> T put(String key, T val);

    /**
     * 删除
     *
     * @param key key
     * @return true 删除成功|false 删除失败
     */
    boolean delete(String key);

    /**
     * cas整数操作，返回当前数，并使其+1
     * 初始为0
     *
     * @param key key
     * @return value
     */
    default Long casInc(String key) {
        throw new UnsupportedOperationException("No implementation in this class.");
    }
}
