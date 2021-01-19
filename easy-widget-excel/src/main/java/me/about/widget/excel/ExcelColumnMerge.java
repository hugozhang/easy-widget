package me.about.widget.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2021/01/19 15:26
 * @Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface ExcelColumnMerge {

    int[] mergeCols() default {0,0,0,0};

    String mergeColsText() default "合并单元后标题";

}
