package me.about.widget.cache.core;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.about.widget.cache.entity.CacheInvokeConfig;
import me.about.widget.cache.stats.CacheStatistics;
import me.about.widget.cache.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
                logger.error("[PUT Cache Thread] rejectedExecution:{}", r);
                r.run();
            });
    }

    private Object lookup(Object key) {
        String cacheKey = getKey(key);
        Object value = localCacheService.get(cacheKey);
        if (value != null) {
            logger.debug("[GET Cache - Local] key:{}." ,cacheKey);
            return value;
        }
        value = remoteCacheService.get(cacheKey);
        if (value != null) {
            logger.debug("[GET Cache - Remote] key:{}." ,cacheKey);
            localCacheService.put(cacheKey,value);
            stats.cacheSizeIncrease();
        }
        return value;
    }


    private List<Object> lookupAll(List<String> keys,CacheInvokeConfig invokeConfig) {
        // 考虑 a in('a1','a2') or b in('a1','a2')情况
        // 以确定这条记录是哪个字段查询出来的记录,然后以它为key
        // 所以查询的时候 key要是(前缀+列值)的组合 存在一对多

        List<String> newKeys = keys.stream()
                .map(e -> getKey(invokeConfig.getCacheKey() + Constants.JOIN_ON + e))
                .collect(Collectors.toList());

        // 有可能本地缓存只有部分 有可能一部分在本地，一部分在远程，所以只要和key数量不一致都走远程查再查更新
        List<Object> valueList = localCacheService.getAll(newKeys);

        if (valueList.size() == keys.size()) {
            logger.debug("[GET ALL Cache - Local] key:{}." ,newKeys);
            return flatValueList(valueList);
        }

        // 找到redis中存在的key有值,支持赋值给本地缓存
        valueList = remoteCacheService.getAll(newKeys);

        Map<String,Object> nonNullKeyValues = new HashMap<>();
        for (int i = 0; i < valueList.size(); i++) {
            Object value = valueList.get(i);
            if (value != null) {
                nonNullKeyValues.put(newKeys.get(i),value);
            }
        }

        if (!nonNullKeyValues.isEmpty()) {
            logger.debug("[PUT ALL Cache - Remote] key:{}." ,nonNullKeyValues.keySet());
            localCacheService.putAll(nonNullKeyValues,invokeConfig.getExpire(),invokeConfig.getTimeUnit());
            stats.cacheSizeIncrease();
        }

        return flatValueList(valueList);
    }

    private static List<Object> flatValueList(List<Object> valueList) {
        List<Object> flatValueList = new ArrayList<>();
        for (Object element : valueList) {
            if (element instanceof List) {
                flatValueList.addAll((List<?>) element);
            } else {
                flatValueList.add(element);
            }
        }
        return flatValueList;
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
    public List<Object> getAll(List<String> keys, CacheInvokeConfig invokeConfig) {
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        stats.requestMade();
        String lockKey = getLockKey(keys);
        ReentrantLock lock = getLockForKey(lockKey);
        lock.lock();
        try {
            return lookupAll(keys,invokeConfig);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new IllegalStateException(e);
        } finally {
            lock.unlock();
            locks.remove(lockKey);
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
            logger.debug("[PUT Cache] finish,key:{},value:{},elapse:{} ms.", key, value,end - start);
        });
    }

    private String getLockKey(Collection<String> keys) {
        List<String> sortedKeys = keys.stream().sorted().collect(Collectors.toList());
        return Joiner.on("#").join(sortedKeys);
    }

    @Override
    public void putAll(Map<String, Object> keyValues, CacheInvokeConfig invokeConfig) {
        long start = System.currentTimeMillis();
        CompletableFuture.runAsync(() -> {
            // 创建一个新的Map来存放更新后的键值对
            Map<String, Object> updatedKeyValues = new HashMap<>();
            for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                String newKey = getKey(invokeConfig.getCacheKey() + Constants.JOIN_ON + entry.getKey());
                updatedKeyValues.put(newKey, entry.getValue());
            }

            String lockKey = getLockKey(updatedKeyValues.keySet());
            ReentrantLock lock = getLockForKey(lockKey);
            lock.lock();
            try {
                this.localCacheService.putAll(updatedKeyValues,invokeConfig.getExpire(),invokeConfig.getTimeUnit());
                this.remoteCacheService.putAll(updatedKeyValues,invokeConfig.getExpire(),invokeConfig.getTimeUnit());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                lock.unlock();
                locks.remove(lockKey);
            }
        }, executor).whenComplete((s, throwable) -> {
            long end = System.currentTimeMillis();
            logger.debug("[PUT ALL Cache] finish,elapse:{} ms.",end - start);
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


    private String getKey(Object key) {
        String cacheKey = key.toString();
        return this.name.concat(Constants.JOIN_ON).concat(cacheKey);
    }
}
