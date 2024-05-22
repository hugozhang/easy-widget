package me.about.widget.cache.core;

import java.util.concurrent.TimeUnit;

public class CaffeineCache extends CacheDecorator {

    protected com.github.benmanes.caffeine.cache.Cache<Object,Object> caffeineCache;

    public CaffeineCache(Cache cache,com.github.benmanes.caffeine.cache.Cache<Object,Object> caffeineCache) {
        super(cache);
        this.caffeineCache = caffeineCache;
    }

    @Override
    public Object get(Object key) {
        Object value = super.get(key);
        if (value != null) {
            return value;
        }
        return caffeineCache.getIfPresent(key);
    }

    @Override
    public void put(Object key, Object value, Long expire, TimeUnit timeUnit) {
        super.put(key,value,expire,timeUnit);
        this.caffeineCache.put(key,value);
    }

    @Override
    public void remove(Object key) {
        super.remove(key);
        this.caffeineCache.invalidate(key);
    }
}
