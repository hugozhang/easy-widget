package me.about.widget.cache.redis;

import me.about.widget.cache.CacheService;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 15:05
 * @Description:
 */
public class RedisCacheService implements CacheService {

    private RedisTemplate<String, Serializable> redisTemplate;

    public RedisCacheService(RedisTemplate<String, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Hash hash(String key) {
        return Hash.build().redisTemplate(redisTemplate).key(key);
    }
}
