package me.about.widget.mybatis.sensitive.annotation;

import me.about.widget.mybatis.sensitive.handler.AesAlgoHandler;
import me.about.widget.mybatis.sensitive.handler.Handler;

import java.lang.annotation.*;

/**
 * 敏感数据标识
 *
 * @author: hugo.zxh
 * @date: 2023/03/20 17:17
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface Sensitive {

    String[] fields() default {};

    Class<? extends Handler> handler() default AesAlgoHandler.class;

}
