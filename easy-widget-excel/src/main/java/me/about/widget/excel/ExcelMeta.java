package me.about.widget.excel;

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

    ExcelColumnMerge[] mergeCols() default {};

    ExcelRowMerge[] mergeRows() default {};
}
