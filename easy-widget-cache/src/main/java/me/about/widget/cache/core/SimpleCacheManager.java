package me.about.widget.cache.core;

import me.about.widget.cache.util.Constants;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 多级缓存组合
 *
 * @author: hugo.zxh
 * @date: 2023/02/09 15:32
 * @description:
 */
public class SimpleCacheManager implements CacheManager {
    private final String name;

    protected  Cache cache;

    public SimpleCacheManager(String name, RedisTemplate<Object,Object> redisTemplate, com.github.benmanes.caffeine.cache.Cache<Object,Object> caffeineCache) {
        this.name = name;
        this.cache = new CaffeineCache(new RedisCache(new EmptyCache(),redisTemplate),caffeineCache);
    }

    @Override
    public Object get(Object key) {
        String cacheKey = getKey(key);
        return this.cache.get(cacheKey);
    }

    @Override
    public void put(Object key, Object value, Long expire, TimeUnit timeUnit) {
        String cacheKey = getKey(key);
        this.cache.put(cacheKey,value,expire,timeUnit);
    }

    @Override
    public void remove(Object key) {
        String cacheKey = getKey(key);
        this.cache.remove(cacheKey);
    }

    @Override
    public void clear() {

    };

    private String getKey(Object key) {
        String cacheKey = key.toString();
        return this.name.concat(Constants.JOIN_ON).concat(cacheKey);
    }
}
