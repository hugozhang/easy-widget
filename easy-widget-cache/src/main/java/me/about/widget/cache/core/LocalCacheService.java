package me.about.widget.cache.core;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public List<Object> getAll(List<String> keyList) {

       return new ArrayList<>(caffeineCache.getAllPresent(keyList).values());
    }

    @Override
    public void put(String key, Object value) {
        this.caffeineCache.put(key,value);
    }

    @Override
    public void putAll(Map<String, Object> keyValues, Long expire, TimeUnit timeUnit) {
        caffeineCache.putAll(keyValues);
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
