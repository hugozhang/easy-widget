package me.about.widget.cache.core;

import java.util.concurrent.TimeUnit;

public interface Cache {

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
    void remove(Object key);

}
