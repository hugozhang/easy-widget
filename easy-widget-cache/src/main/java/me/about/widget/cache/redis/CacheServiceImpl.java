package me.about.widget.cache.redis;

import me.about.widget.cache.CacheService;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 11:29
 * @Description:
 */
public class CacheServiceImpl implements CacheService {

    private RedisTemplate<String, Serializable> redisTemplate;

    @Override
    public boolean hasKey(String key) {
        return false;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void put(String key, Object value) {

    }

    @Override
    public void put(String key, Object value, long expire, TimeUnit timeUnit) {

    }

    @Override
    public void delete(String key) {

    }
}
