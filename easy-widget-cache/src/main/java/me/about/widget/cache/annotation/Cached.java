package me.about.widget.cache.annotation;

import me.about.widget.cache.core.CacheOp;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存注解
 *
 * @author: hugo.zxh
 * @date: 2022/06/14 14:36
 * @description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Cached {

    // ===== 共用 =============
    String name() default "";

    String key();

    CacheOp cacheOp() default CacheOp.GET;

    // ====== put 相关缓存配置 =============
    long expire() default 6;

    TimeUnit timeUnit() default TimeUnit.HOURS;

    // ====== 空值缓存配置  默认存一天
    long emptyExpire() default -1;

    TimeUnit emptyTimeUnit() default TimeUnit.DAYS;

}
