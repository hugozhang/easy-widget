package me.about.widget.excel.annotation;

import me.about.widget.excel.writer.CellFormatter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.lang.annotation.ElementType;

/**
 * 单元格 格式化
 *
 * @author: hugo.zxh
 * @date: 2021/01/19 15:26
 * @description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface ExcelCellFormat {

    // 自定义格式化
    Class<? extends CellFormatter> format() default CellFormatter.class;

    // 透传的后缀字符串
    String payload() default "";

}
