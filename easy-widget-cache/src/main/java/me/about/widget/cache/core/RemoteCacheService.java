package me.about.widget.cache.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public List<Object> getAll(List<String> keyList) {
//        redisTemplate.opsForValue().multiGet(keyList);
        // 执行流水线操作
//        redisTemplate.executePipelined((RedisCallback<List<Object>>) connection ->
//            keyList.stream()
//             .map(key -> connection.get(key.getBytes()))
//             .collect(Collectors.toList()));

        // 定义每批处理的key数量
        int batchSize = 10;
        List<Object> results = new ArrayList<>();
        for (int i = 0; i < keyList.size(); i += batchSize) {
            // 获取一批key
            List<String> batchKeys = keyList.subList(i, Math.min(i + batchSize, keyList.size()));
            // 执行批量查询
            List<Object> batchValues = redisTemplate.opsForValue().multiGet(batchKeys);
            if (batchValues != null) {
                results.addAll(batchValues);
            }
        }
        return results;

    }

    @Override
    public void put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<String, Object> keyValues,Long expire, TimeUnit timeUnit) {
        // 使用multiSet批量设置键值对
//        redisTemplate.opsForValue().multiSet(keyValues);
        // 使用executePipelined批量设置过期时间
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisSerializer serializer = new StringRedisSerializer();
            keyValues.forEach((key, value) -> {
                byte[] keyBytes = serializer.serialize(key);
                byte[] valueBytes = JSON.toJSONBytes(value, SerializerFeature.WriteClassName);
                if (keyBytes != null) {
                    connection.set(keyBytes, valueBytes);
                    connection.expire(keyBytes, timeUnit.toSeconds(expire));
                }
            });
            return null; // 流水线要求返回null
        });
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
