package me.about.widget.cache.redis;

import me.about.widget.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 15:05
 * @Description:
 */
public class RedisCache implements Cache {

    private RedisTemplate<String, Serializable> redisTemplate;

    public RedisCache(RedisTemplate<String, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Hash hash(String key) {
        return Hash.build().redisTemplate(redisTemplate).key(key);
    }

}
