package me.about.widget.mybatis.function.multidatasource;

import java.lang.annotation.*;

/**
 * 多数据源注解
 *
 * @author: hugo.zxh
 * @date: 2022/05/20 11:25
 * @description:
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbSwitch {

    DbTypeEnum value() default DbTypeEnum.DB1;

}
