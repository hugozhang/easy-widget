package me.about.widget.mybatis.sensitive.annotation;

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
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface SensitiveMapper {
}
