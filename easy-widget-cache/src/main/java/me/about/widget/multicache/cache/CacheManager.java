package me.about.widget.multicache.cache;

import java.util.concurrent.TimeUnit;

/**
 * 缓存管理
 *
 * @author: hugo.zxh
 * @date: 2023/02/09 16:44
 * @description:
 */
public interface CacheManager {

    /**
     * 从缓存读取
     * @param key
     * @param <V>
     * @return
     */
    <V> V get(Object key);

    /**
     * 设置缓存
     * @param key
     * @param value
     * @param expire
     * @param timeUnit
     */
    void put(Object key, Object value, Long expire, TimeUnit timeUnit);

    /**
     * 清除缓存
     * @param key
     */
    void evict(Object key);

    /**
     * 清除所有缓存
     */
    void clear();

}
