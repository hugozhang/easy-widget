package me.about.widget.multicache.annotation;

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
public @interface MultiLevelCache {

    // ===== 共用 =============
    String keyPrefix() default "";

    String key();

    MultiLevelTypeEnum type() default MultiLevelTypeEnum.PUT;

    // ====== put 相关缓存配置 =============
    long expire() default 6;

    TimeUnit timeUnit() default TimeUnit.HOURS;

    // ====== 空值缓存配置  默认存一天
    long emptyExpire() default -1;

    TimeUnit emptyTimeUnit() default TimeUnit.DAYS;

}
