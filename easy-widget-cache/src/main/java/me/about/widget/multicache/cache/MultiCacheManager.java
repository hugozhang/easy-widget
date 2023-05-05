package me.about.widget.multicache.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多级缓存组合
 *
 * @author: hugo.zxh
 * @date: 2023/02/09 15:32
 * @description:
 */
public class MultiCacheManager implements CacheManager {

    private Logger logger = LoggerFactory.getLogger(MultiCacheManager.class);

    private String name;

    private RedisTemplate<String,Object> redisTemplate;

    private Cache<String,Object> caffeineCache;

    public MultiCacheManager(String name, RedisTemplate redisTemplate, Cache caffeineCache) {
        this.name = name;
        this.redisTemplate = redisTemplate;
        this.caffeineCache = caffeineCache;
    }

    private Object lookup(Object key) {
        String cacheKey = getKey(key);
        Object value = caffeineCache.getIfPresent(cacheKey);
        if (value != null) {
            if (Objects.equals(value,NullValue.INSTANCE)) {
                return null;
            }
            logger.info("From local cache(caffeine),key:{},value:{}." ,cacheKey,value);
            return value;
        }
        value = redisTemplate.opsForValue().get(cacheKey);
        if (value != null) {
            logger.info("From remote cache(redis),key:{},value:{}." ,cacheKey,value);
            caffeineCache.put(cacheKey,value);
        }
        return value;
    }


    @Override
    public <T> T get(Object key) {
        Object value = lookup(key);
        if (value != null) {
            return (T) value;
        }
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            value = lookup(key);
            return (T) value;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new IllegalStateException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(Object key, Object value, Long expire, TimeUnit timeUnit) {
        String cacheKey = getKey(key);
        this.redisTemplate.opsForValue().set(cacheKey,value,expire,timeUnit);
        if (value == null) {
            this.caffeineCache.put(cacheKey,NullValue.INSTANCE);
        } else {
            this.caffeineCache.put(cacheKey,value);
        }
    }

    @Override
    public void evict(Object key) {
        String cacheKey = getKey(key);
        this.redisTemplate.delete(cacheKey);
        this.caffeineCache.invalidate(cacheKey);
    }

    @Override
    public void clear() {
        Set<String> keys = this.redisTemplate.keys(this.name.concat(":"));
        for (String key : keys) {
            this.redisTemplate.delete(key);
        }
        this.caffeineCache.invalidateAll();
    };

    private String getKey(Object key) {
        String cacheKey = key.toString();
        return this.name.concat(":").concat(cacheKey);
    }
}
