package me.about.widget.cache.core;

import me.about.widget.cache.stats.CacheStatistics;
import me.about.widget.cache.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Logger logger = LoggerFactory.getLogger(MultiCacheManager.class);

    private String name;

    private CacheService localCacheService;

    private CacheService remoteCacheService;

    private CacheStatistics stats;


    private MultiCacheManager() {

    }


    public MultiCacheManager(String name, CacheService localCacheService, CacheService remoteCacheService) {
        this.name = name;
        this.localCacheService = localCacheService;
        this.remoteCacheService = remoteCacheService;
        this.stats = new CacheStatistics();
    }

    private Object lookup(Object key) {
        String cacheKey = getKey(key);
        Object value = localCacheService.get(cacheKey);
        if (value != null) {
            logger.info("[Local Cache] key:{},value:{}." ,cacheKey,value);
            return value;
        }
        value = remoteCacheService.get(cacheKey);
        if (value != null) {
            logger.info("[Remote Cache] key:{},value:{}." ,cacheKey,value);
            localCacheService.put(cacheKey,value);
            stats.cacheSizeIncrease();
        }
        return value;
    }


    @Override
    public Object get(Object key) {
        stats.requestMade();
        Object value = lookup(key);
        if (value != null) {
            stats.cacheHit();
            return value;
        }
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            value = lookup(key);
            return value;
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
        this.remoteCacheService.put(cacheKey,value,expire,timeUnit);
        this.localCacheService.put(cacheKey,value);
        stats.cacheSizeIncrease();
    }

    @Override
    public void remove(Object key) {
        String cacheKey = getKey(key);
        this.remoteCacheService.remove(cacheKey);
        this.localCacheService.remove(cacheKey);
        stats.cacheEviction();
        stats.cacheSizeDecrease();
    }

    @Override
    public void clear() {
//        Set<String> keys = Optional.ofNullable(this.redisTemplate.keys(this.name.concat(Constants.JOIN_ON))).orElse(new HashSet<>());
//        for (String key : keys) {
//            this.redisTemplate.delete(key);
//        }
//        this.caffeineCache.invalidateAll();
    }

    @Override
    public void eventHandle(Object key) {
        this.localCacheService.remove(key.toString());
        stats.cacheEviction();
        stats.cacheSizeDecrease();
    }

    @Override
    public String getStats() {
        return stats.displayStatistics();
    }

    ;

    private String getKey(Object key) {
        String cacheKey = key.toString();
        return this.name.concat(Constants.JOIN_ON).concat(cacheKey);
    }
}
