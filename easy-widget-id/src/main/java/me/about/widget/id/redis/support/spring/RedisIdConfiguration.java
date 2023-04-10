package me.about.widget.id.redis.support.spring;

import me.about.widget.id.IdGenerator;
import me.about.widget.id.redis.RedisIdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * 分布式id redis + spring
 *
 * @author: hugo.zxh
 * @date: 2020/11/30 16:04
 * @description:
 */

@Configuration
@ConditionalOnClass(StringRedisTemplate.class)
public class RedisIdConfiguration {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    @ConditionalOnMissingBean
    public IdGenerator idGenerator() {
        return new RedisIdGenerator(stringRedisTemplate);
    }

}
