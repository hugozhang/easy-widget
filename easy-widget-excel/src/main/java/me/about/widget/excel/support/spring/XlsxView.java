package me.about.widget.excel.support.spring;

/**
 * spring view support
 *
 * @author: hugo.zxh
 * @date: 2020/11/01 23:46
 * @description:
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface XlsxView {

    String fileName() default "download.xlsx";//file name

    Class inputClass();

}
