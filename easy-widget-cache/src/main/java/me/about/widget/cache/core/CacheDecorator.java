package me.about.widget.cache.core;

import java.util.concurrent.TimeUnit;

public abstract class CacheDecorator implements Cache {

    protected Cache cache;

    public CacheDecorator(Cache cache) {
        this.cache = cache;
    }

    @Override
    public Object get(Object key) {
        return cache.get(key);
    }

    @Override
    public void put(Object key, Object value, Long expire, TimeUnit timeUnit) {
        cache.put(key,value,expire,timeUnit);
    }

    @Override
    public void remove(Object key) {
        cache.remove(key);
    }
}
