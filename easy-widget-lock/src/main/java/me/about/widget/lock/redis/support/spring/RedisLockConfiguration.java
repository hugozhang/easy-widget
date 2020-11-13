package me.about.widget.lock.redis.support.spring;

import me.about.widget.lock.redis.RedisLockContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/13 18:03
 * @Description:
 */
@Configuration
public class RedisLockConfiguration {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    public RedisLockContext redisLockContext() {
        return new RedisLockContext(stringRedisTemplate);
    }

    @Bean
    public RedisLockAspect redisLockAspect() {
        return new RedisLockAspect();
    }
}
