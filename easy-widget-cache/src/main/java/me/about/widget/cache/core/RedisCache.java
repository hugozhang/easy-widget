package me.about.widget.cache.core;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisCache extends CacheDecorator {

    protected RedisTemplate<Object,Object> redisTemplate;

    public RedisCache(Cache cache, RedisTemplate<Object,Object> redisTemplate) {
        super(cache);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object get(Object key) {
        Object value = super.get(key);
        if (value != null) {
            return value;
        }
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void put(Object key, Object value, Long expire, TimeUnit timeUnit) {
        super.put(key,value,expire,timeUnit);
        redisTemplate.opsForValue().set(key,value,expire,timeUnit);
    }

    @Override
    public void remove(Object key) {
        super.remove(key);
        this.redisTemplate.delete(key);
    }
}
