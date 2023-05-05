package me.about.widget.retrofit2.annotation;

import java.lang.annotation.*;

/**
 * 实例化注解标识
 *
 * @author: hugo.zxh
 * @date: 2023/03/28 15:36
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RetrofitHttpClient {

    String baseUrl();

}
