package me.about.widget.multicache.spring.boot.autoconfig;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import me.about.widget.multicache.cache.MultiCacheManager;
import me.about.widget.multicache.util.GenericFastJsonRedisSerializerExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 *
 * @author: hugo.zxh
 * @date: 2022/12/15 11:30
 * @description:
 */
@Slf4j
@ConditionalOnClass(value = {RedisTemplate.class,Cache.class})
@Configuration
public class MultiCacheConfig {

    @Value("${me.about.widget.multi-cache.namespace:cache.multi}")
    private String cacheName;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisTemplate<String, Object> fastJsonRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        GenericFastJsonRedisSerializerExt genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializerExt();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(genericFastJsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(genericFastJsonRedisSerializer);
        return redisTemplate;
    }

    @Bean
    public Cache<?,?> caffeineCache() {
        return Caffeine.newBuilder().recordStats()
                .initialCapacity(100)
                .maximumSize(500_000)
                .weakKeys()
                .weakValues()
                .removalListener(((key, value, cause) -> log.debug("key:{}, was removed, cause:{}", key, cause)))
                .expireAfterWrite(10, TimeUnit.MINUTES).build();
    }

    @Bean
    public MultiCacheManager multiCacheManager(@Autowired RedisTemplate fastJsonRedisTemplate, @Autowired Cache caffeineCache) {
        return new MultiCacheManager(cacheName,fastJsonRedisTemplate,caffeineCache);
    }

}
