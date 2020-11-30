package me.about.widget.cache.redis;

import me.about.widget.cache.CacheService;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 15:05
 * @Description:
 */
public class RedisCacheService implements CacheService {

    private Map<String, BoundHashOperations<String, Object, Object>> cacheHash = new ConcurrentHashMap();

    private RedisTemplate redisTemplate;

    public RedisCacheService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Hash hash(String key) {
        BoundHashOperations<String, Object, Object> hashOperations = cacheHash.computeIfAbsent(key, ns -> {
            return redisTemplate.boundHashOps(ns);
        });
        return new Hash(hashOperations);
    }
}
