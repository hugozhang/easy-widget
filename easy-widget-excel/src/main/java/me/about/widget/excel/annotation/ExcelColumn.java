package me.about.widget.excel.annotation;

import java.lang.annotation.*;

/**
 * 列注解
 *
 * @author: hugo.zxh
 * @date: 2020/11/01 23:13
 * @description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface ExcelColumn {
    // 列名
    String name();

    // 宽度
    int width() default 30;

    // 分组 双行表头
    String groupName() default "0";

    // 原生格式化
    String format() default "yyyy-MM-dd HH:mm:ss";

    // 排序字段
    int order() default 0;

    // 相同数据单元格是否需要合并
    boolean cellMerge() default false;

    // 单元格格式化
    ExcelCellFormat cellFormat() default @ExcelCellFormat;

}
