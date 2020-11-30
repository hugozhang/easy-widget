package me.about.widget.cache.redis;

import org.springframework.data.redis.core.BoundHashOperations;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 14:38
 * @Description:
 */
public class Hash {

    private BoundHashOperations<String, Object, Object> hashOperations;

    public Hash(BoundHashOperations hashOperations) {
        this.hashOperations = hashOperations;
    }

    public boolean hasKey(String key) {
        return hashOperations.hasKey(key);
    }

    public Object get(String key) {
        return hashOperations.get(key);
    }

    public void put(String key, Object value) {
        put(key,value,2,TimeUnit.HOURS);
    }

    public void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        hashOperations.put(key,value);
        hashOperations.expire(timeout,timeUnit);
    }

    public void delete(String... key) {
        hashOperations.delete(key);
    }
}
