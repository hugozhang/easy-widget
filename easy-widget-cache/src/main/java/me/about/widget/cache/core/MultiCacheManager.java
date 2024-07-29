package me.about.widget.cache.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.about.widget.cache.stats.CacheStatistics;
import me.about.widget.cache.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
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

    private final Map<Object, ReentrantLock> locks = new ConcurrentHashMap<>();

    private String name;

    private CacheService localCacheService;

    private CacheService remoteCacheService;

    private CacheStatistics stats;

    private Executor executor;

    private MultiCacheManager() {

    }


    public MultiCacheManager(String name, CacheService localCacheService, CacheService remoteCacheService) {
        this.name = name;
        this.localCacheService = localCacheService;
        this.remoteCacheService = remoteCacheService;
        this.stats = new CacheStatistics();
        this.executor = new ThreadPoolExecutor(5, 20, 10, TimeUnit.SECONDS
            , new LinkedBlockingQueue<>(200)
            , new ThreadFactoryBuilder().setNameFormat("PUT Cache Thread").build()
            , (r, executor) -> {
                r.run();
                logger.error("[PUT Cache Thread] rejectedExecution:{}", r);
            });
    }

    private Object lookup(Object key) {
        String cacheKey = getKey(key);
        Object value = localCacheService.get(cacheKey);
        if (value != null) {
            logger.info("[GET Cache - Local] key:{}." ,cacheKey);
            return value;
        }
        value = remoteCacheService.get(cacheKey);
        if (value != null) {
            logger.info("[GET Cache - Remote] key:{}." ,cacheKey);
            localCacheService.put(cacheKey,value);
            stats.cacheSizeIncrease();
        }
        return value;
    }

    private ReentrantLock getLockForKey(Object key) {
        return locks.computeIfAbsent(key, k -> new ReentrantLock());
    }


    @Override
    public Object get(Object key) {
        stats.requestMade();
        Object value = lookup(key);
        if (value != null) {
            stats.cacheHit();
            return value;
        }
        ReentrantLock lock = getLockForKey(key);
        lock.lock();
        try {
            return lookup(key);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new IllegalStateException(e);
        } finally {
            lock.unlock();
            locks.remove(key);
        }
    }

    @Override
    public void put(Object key, Object value, Long expire, TimeUnit timeUnit) {
        // 整个过程考虑异步写，因为在它之前的调用都是原始方法从数据库拿，要么拿到结果，要么没有结果，
        // 所以这个时候拿到的结果可以直接返回，没有必要等待缓存
        // 于此同时在读的时候，考虑第一次正常读，第二次加锁读
        long start = System.currentTimeMillis();
        CompletableFuture.runAsync(() -> {
            ReentrantLock lock = getLockForKey(key);
            lock.lock();
            try {
                String cacheKey = getKey(key);
                this.remoteCacheService.put(cacheKey, value, expire, timeUnit);
                this.localCacheService.put(cacheKey, value);
                stats.cacheSizeIncrease();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                lock.unlock();
                locks.remove(key);
            }
        }, executor).whenComplete((s, throwable) -> {
            long end = System.currentTimeMillis();
            logger.info("[PUT Cache] finish,key:{},value:{},elapse:{} ms.", key, value,end - start);
        });
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
