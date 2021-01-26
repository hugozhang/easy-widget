package me.about.widget.excel.annotation;

import me.about.widget.excel.writer.CellFormatter;
import me.about.widget.excel.writer.CustomerCellFormatter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2021/01/19 15:26
 * @Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ANNOTATION_TYPE})
public @interface ExcelCellFormat {

    // 自定义格式化
    Class<? extends CellFormatter> format() default CustomerCellFormatter.class;

    // 透传的后缀字符串
    String payload() default "";

}
