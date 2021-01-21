package me.about.widget.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/01 23:13
 * @Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface ExcelColumn {
    // 列名
    String name();

    // 宽度
    int width() default 30;

    // 格式化
    String format() default "yyyy-MM-dd HH:mm:ss";
}
