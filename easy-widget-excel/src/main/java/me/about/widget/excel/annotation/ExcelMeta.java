package me.about.widget.excel.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2021/01/19 10:43
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelMeta {

    int headEndIndex() default 0;

    ExcelCellMerge[] mergeCells() default {};

}
