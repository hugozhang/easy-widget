package me.about.widget.cache.core;

import java.util.concurrent.TimeUnit;

public interface CacheService {

    /**
     * 从缓存读取 一对一  一对多
     * @param key
     * @return
     */
    Object get(String key);


    void put(String key, Object value);

    /**
     * 设置缓存
     * @param key
     * @param value
     * @param expire
     * @param timeUnit
     */
    void put(String key, Object value, Long expire, TimeUnit timeUnit);

    /**
     * 清除缓存
     * @param key
     */
    void remove(String key);

}
