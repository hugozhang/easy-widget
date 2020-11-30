package me.about.widget.cache.redis.support.spring.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/30 17:09
 * @Description:
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MyCachePut {

    String group();

    String key();

    long expire() default 6;

    TimeUnit timeUnit() default TimeUnit.HOURS;

}
