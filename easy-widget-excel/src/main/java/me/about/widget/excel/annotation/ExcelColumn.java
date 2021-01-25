package me.about.widget.excel.annotation;

import java.lang.annotation.*;

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

    // 原生格式化
    String format() default "yyyy-MM-dd HH:mm:ss";

    // 单元格格式化
    ExcelCellFormat cellFormat() default @ExcelCellFormat;

}
