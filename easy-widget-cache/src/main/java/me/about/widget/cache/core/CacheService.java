package me.about.widget.cache.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface CacheService {

    /**
     * 从缓存读取 一对一  一对多
     */
    Object get(String key);


    /**
     * 从缓存读取 多对多
     */
    List<Object> getAll(List<String> keyList);


    void put(String key, Object value);


    void putAll(Map<String,Object> keyValues,Long expire, TimeUnit timeUnit);

    /**
     * 设置缓存
     */
    void put(String key, Object value, Long expire, TimeUnit timeUnit);

    /**
     * 清除缓存
     */
    void remove(String key);

}
