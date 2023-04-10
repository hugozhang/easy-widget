package me.about.widget.lock.redis.annotation;

import java.lang.annotation.*;

/**
 * 分布式锁注解
 *
 * @author: hugo.zxh
 * @date: 2020/11/13 10:15
 * @description:
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface DLock {
    String key();
}
