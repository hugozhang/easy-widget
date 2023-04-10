package me.about.widget.lock.redis.annotation;

import me.about.widget.lock.redis.support.spring.RedisLockConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * spring boot 注解开启分布式锁
 *
 * @author: hugo.zxh
 * @date: 2020/11/13 18:04
 * @description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(RedisLockConfiguration.class)
public @interface EnableDLock {
}
