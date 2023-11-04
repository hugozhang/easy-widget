package me.about.widget.routing.spring.annotation;

import java.lang.annotation.*;

/**
 * 多数据源注解
 *
 * @author: hugo.zxh
 * @date: 2023/10/31 15:35
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface HintRouting {
    String value();
}
