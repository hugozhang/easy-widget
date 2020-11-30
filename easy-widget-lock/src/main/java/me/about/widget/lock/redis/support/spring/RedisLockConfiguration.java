package me.about.widget.lock.redis.support.spring;

import me.about.widget.lock.LockContext;
import me.about.widget.lock.redis.RedisLockContext;
import me.about.widget.spring.expression.SpELAspectContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
@ConditionalOnClass(StringRedisTemplate.class)
public class RedisLockConfiguration {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    @ConditionalOnMissingBean
    public LockContext lockContext() {
        return new RedisLockContext(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisLockAspect redisLockAspect() {
        return new RedisLockAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpELAspectContext spELAspectContext() {
        return new SpELAspectContext();
    }
}
