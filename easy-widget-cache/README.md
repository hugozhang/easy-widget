
组件介绍
````
1. spring cache 重新实现支持 不同key的过期时间

2. 多级缓存实现（解决业务上一对一、一对多的情况）
  1) 空值缓存（有时效控制，取决业务上）

````

例子：
````
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 使用 GenericFastJsonRedisSerializer 替换默认序列化
        GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        // 设置key和value的序列化规则
        redisTemplate.setKeySerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.setValueSerializer(genericFastJsonRedisSerializer);
        // 设置hashKey和hashValue的序列化规则
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(genericFastJsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public Cache caffeineCache() {
        return Caffeine.newBuilder().recordStats()
                .removalListener(((key, value, cause) -> log.info("key:{}, was removed, cause:{}", key, cause)))
                .expireAfterWrite(3, TimeUnit.SECONDS).build();
    }
}

````