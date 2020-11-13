package me.about.widget.cache.redis;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 14:38
 * @Description:
 */
public class Hash {

    private RedisTemplate<String, Serializable> redisTemplate;

    private BoundHashOperations<String, Object, Object> hash;

    private Hash() {}

    public Hash redisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        return this;
    }

    public Hash key(String key) {
        this.hash = this.redisTemplate.boundHashOps(key);
        return this;
    }

    public static Hash build() {
        return new Hash();
    }

    public boolean hasKey(String key) {
        return hash.hasKey(key);
    }

    public Object get(String key) {
        return hash.get(key);
    }

    public void put(String key, Object value) {
        put(key,value,10,TimeUnit.MINUTES);
    }

    public void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        hash.put(key,value);
        hash.expire(timeout,timeUnit);
    }

    public void delete(String... key) {
        hash.delete(key);
    }
}
