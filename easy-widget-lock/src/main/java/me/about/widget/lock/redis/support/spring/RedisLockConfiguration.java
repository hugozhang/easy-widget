package me.about.widget.lock.redis.support.spring;

import me.about.widget.lock.LockContext;
import me.about.widget.lock.redis.RedisLockContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * spring 上下文
 *
 * @author: hugo.zxh
 * @date: 2020/11/13 18:03
 * @description:
 */
@Configuration
@ConditionalOnClass(StringRedisTemplate.class)
public class RedisLockConfiguration {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    @ConditionalOnMissingBean
    public LockContext lockContext() {
        return new RedisLockContext(stringRedisTemplate);
    }

}
