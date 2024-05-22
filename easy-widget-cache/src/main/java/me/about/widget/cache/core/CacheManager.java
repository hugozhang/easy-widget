package me.about.widget.cache.core;

import java.util.concurrent.TimeUnit;

/**
 * 缓存管理   缓存里面存放的都是单一对象，集合对象也会转成单一对象
 *
 * @author: hugo.zxh
 * @date: 2023/02/09 16:44
 * @description:
 */
public interface CacheManager {

    /**
     * 从缓存读取 一对一  一对多
     * @param key
     * @return
     */
    Object get(Object key);

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
