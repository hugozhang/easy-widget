package me.about.widget.cache.spring.boot.autoconfig;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import me.about.widget.cache.core.CacheManager;
import me.about.widget.cache.core.LocalCacheService;
import me.about.widget.cache.core.MultiCacheManager;
import me.about.widget.cache.core.RemoteCacheService;
import me.about.widget.cache.support.GenericFastJsonRedisSerializerExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyspaceEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
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

    @Value("${me.about.widget.multi-cache.namespace:cache}")
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
    public RedisMessageListenerContainer redisMessageListenerContainer(@Autowired CacheManager cacheManager) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        KeyspaceEventMessageListener listener = new KeyspaceEventMessageListener(container) {
            @Override
            protected void doHandleMessage(Message message) {
                String body = new String(message.getBody());
                System.out.println(body);
                cacheManager.remove(body);
            }
        };

        MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
        container.setConnectionFactory(redisConnectionFactory);

        // 订阅删除事件、过期事件
        container.addMessageListener(adapter, ImmutableList.of(
                new PatternTopic("__keyevent@0__:del")
                ,new PatternTopic("__keyevent@0__:expired")));
        return container;
    }

    @Bean
    public Cache<String,Object> caffeineCache() {
        return Caffeine.newBuilder().recordStats()
                .initialCapacity(1000)
                .maximumSize(100_000)
//                .weakKeys()
//                .weakValues()
                .removalListener((key, value, cause) ->
                        System.out.println("key:" + key + ",value:" + value + ",删除原因:" + cause))
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public CacheManager cacheManager(@Autowired RedisTemplate<String,Object> fastJsonRedisTemplate, @Autowired Cache<String,Object> caffeineCache) {
        return new MultiCacheManager(cacheName,new LocalCacheService(caffeineCache),new RemoteCacheService(fastJsonRedisTemplate));
    }

}
