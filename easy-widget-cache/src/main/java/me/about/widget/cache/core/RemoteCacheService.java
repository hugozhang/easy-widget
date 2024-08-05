package me.about.widget.cache.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import me.about.widget.cache.util.Constants;
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
        if (keyList.isEmpty()) {
            return Constants.EMPTY_LIST;
        }
        // 分批
        List<Object> results = new ArrayList<>();
        for (int i = 0; i < keyList.size(); i += Constants.REDIS_BATCH_SIZE) {
            List<String> batchKeys = keyList.subList(i, Math.min(i + Constants.REDIS_BATCH_SIZE, keyList.size()));
            List<Object> batchValues = redisTemplate.opsForValue().multiGet(batchKeys);
            if (batchValues != null) {
                results.addAll(batchValues);
            }
        }


//        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//            StringRedisSerializer keySerializer = new StringRedisSerializer();
//            RedisSerializer<Object> valueSerializer = new GenericFastJsonRedisSerializerExt();
//            List<byte[]> bytes = connection.mGet(keyList.stream()
//                    .map(keySerializer::serialize)
//                    .toArray(byte[][]::new));
//            if (bytes == null) {
//                return null;
//            }
//            return bytes
//                    .stream()
//                    .map(valueSerializer::deserialize)
//                    .collect(Collectors.toList());
//        });

        return results;

    }

    @Override
    public void put(String key, Object value) {
        this.redisTemplate.opsForValue().set(key,value);
    }

    @Override
    public void putAll(Map<String, Object> keyValues,Long expire, TimeUnit timeUnit) {
        // 使用multiSet批量设置键值对
//        redisTemplate.opsForValue().multiSet(keyValues);
        batchPut(keyValues, expire, timeUnit);
    }

    private void batchPut(Map<String, Object> keyValues, Long expire, TimeUnit timeUnit) {
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
            return null;
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
