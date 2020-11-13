package me.about.widget.lock.redis.support.spring.annotation;

import me.about.widget.lock.redis.support.spring.RedisLockConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/13 18:04
 * @Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(RedisLockConfiguration.class)
public @interface EnableDLock {
}
