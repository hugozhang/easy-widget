package me.about.widget.retrofit2.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RetryOnFailure {
    int maxRetries() default 3; // 默认重试次数
    long initialDelayMs() default 1000; // 初始延迟时间（毫秒）
}