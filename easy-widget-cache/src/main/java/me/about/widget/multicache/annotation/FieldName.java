package me.about.widget.multicache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  内部缓存动态key 设置
 *
 * @author: hugo.zxh
 * @date: 2022/06/29 17:04
 * @description:
 */

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD,ElementType.PARAMETER} )
public @interface FieldName {
    String value();
}
