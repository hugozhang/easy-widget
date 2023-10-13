package me.about.widget.security.config;

import me.about.widget.security.util.ObjectMapperUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import javax.annotation.Resource;

/**
 * Redis 配置类
 *
 * @author: hugo.zxh
 * @date: 2023/10/13 11:31
 */
@Configuration
public class RedisSpringAuthorizationServerConfiguration {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    private void initRedisTemplate(RedisTemplate redisTemplate) {
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(ObjectMapperUtils.redis());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
    }

    @Bean
    public RedisTemplate<String, RegisteredClient> redisTemplateRegisteredClient() {
        RedisTemplate<String, RegisteredClient> redisTemplate = new RedisTemplate<>();
        initRedisTemplate(redisTemplate);
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, OAuth2Authorization> redisTemplateOAuth2Authorization() {
        RedisTemplate<String, OAuth2Authorization> redisTemplate = new RedisTemplate<>();
        initRedisTemplate(redisTemplate);
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

}
