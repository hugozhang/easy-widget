package me.about.widget.cache.spring.boot.autoconfig;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import me.about.widget.cache.core.*;
import me.about.widget.cache.support.GenericFastJsonRedisSerializerExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

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

    @Value("${me.about.widget.multi-cache.stats.period:20000}")
    private long statsPeriod;

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

    private void eventHandle(CacheManager cacheManager, Message message) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        log.debug("[Cache Event] cache key event: " + channel + "," + body);
        //删除本地缓存
        cacheManager.eventHandle(body);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(CacheManager cacheManager) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        KeyspaceEventMessageListener listener = new KeyspaceEventMessageListener(container) {
            @Override
            protected void doHandleMessage(Message message) {
                eventHandle(cacheManager,message);
            }
        };

        MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
        container.setConnectionFactory(redisConnectionFactory);

        // 订阅删除事件、过期事件
        container.addMessageListener(adapter, ImmutableList.of(
                new PatternTopic("__keyevent@0__:del")
                ,new PatternTopic("__keyevent@0__:set")
                ,new PatternTopic("__keyevent@0__:expired")));
        return container;
    }


    @ConditionalOnProperty(name = "me.about.widget.multi-cache.stats.enable",havingValue = "true")
    @Bean
    public Timer cacheStatsTime(CacheManager cacheManager) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                log.info("[Cache Stats] : " + cacheManager.getStats());
            }
        }, 0, statsPeriod);
        return timer;
    }

    @Bean
    public Cache<String,Object> caffeineCache() {
        return Caffeine.newBuilder().recordStats()
//                .initialCapacity(100)
                .maximumSize(50_000)
//                .weakKeys()
//                .weakValues()
                .removalListener((key, value, cause) ->
                        log.debug("[Remove Cache - Local] key:" + key + ",value:" + value + ",cause:" + cause))
//                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public CacheManager cacheManager(@Autowired CacheService localCacheService,CacheService remoteCacheService) {
        return new MultiCacheManager(cacheName,localCacheService,remoteCacheService);
    }

    @Bean
    public CacheService localCacheService(@Autowired Cache<String,Object> caffeineCache) {
        return new LocalCacheService(caffeineCache);
    }

    @Bean
    public CacheService remoteCacheService(@Autowired RedisTemplate<String,Object> fastJsonRedisTemplate) {
        return new RemoteCacheService(fastJsonRedisTemplate);
    }

}
