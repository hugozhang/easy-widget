package me.about.widget.cache.core;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RemoteCacheService implements CacheService {

    protected RedisTemplate<String,Object> redisTemplate;

    public RemoteCacheService(RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object get(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    @Override
    public void put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(String key, Object value, Long expire, TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set(key,value,expire,timeUnit);
    }

    @Override
    public void remove(String key) {
        this.redisTemplate.delete(key);
    }
}
