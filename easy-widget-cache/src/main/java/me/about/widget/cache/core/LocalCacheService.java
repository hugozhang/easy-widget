package me.about.widget.cache.core;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.concurrent.TimeUnit;

public class LocalCacheService implements CacheService {

    protected Cache<String,Object> caffeineCache;

    public LocalCacheService(Cache<String,Object> caffeineCache) {
        this.caffeineCache = caffeineCache;
    }

    @Override
    public Object get(String key) {
        return caffeineCache.getIfPresent(key);
    }

    @Override
    public void put(String key, Object value) {
        this.caffeineCache.put(key,value);
    }

    @Override
    public void put(String key, Object value, Long expire, TimeUnit timeUnit) {
        this.caffeineCache.put(key,value);
    }

    @Override
    public void remove(String key) {
        this.caffeineCache.invalidate(key);
    }
}
