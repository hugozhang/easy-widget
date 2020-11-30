package me.about.widget.cache.redis.support.spring;

import me.about.widget.cache.CacheService;
import me.about.widget.cache.redis.RedisCacheService;
import me.about.widget.spring.SpELAspectContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/30 16:24
 * @Description:
 */

@Configuration
@ConditionalOnClass(RedisTemplate.class)
public class RedisCacheConfiguration {

    @Resource
    private RedisTemplate redisTemplate;

    @Bean
    @ConditionalOnMissingBean
    public CacheService cacheService() {
        return new RedisCacheService(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisCacheAspect redisCacheAspect() {
        return new RedisCacheAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpELAspectContext spELAspectContext() {
        return new SpELAspectContext();
    }
}
