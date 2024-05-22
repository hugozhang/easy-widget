package me.about.widget.retrofit2.annotation;

import me.about.widget.retrofit2.converter.FastJsonConverterFactory;
import retrofit2.Converter;

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

    Class<? extends Converter.Factory> converterFactory() default FastJsonConverterFactory.class;

}
