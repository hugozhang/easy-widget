package me.about.widget.lock.redis.support.spring.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/13 10:15
 * @Description: 分布式锁注解
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface DLock {
    String key() default "";
}
