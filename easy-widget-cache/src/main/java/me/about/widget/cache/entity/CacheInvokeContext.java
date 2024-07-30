package me.about.widget.cache.entity;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class CacheInvokeContext {

    private Method method;

    private Object[] args;

    private Class<?> returnType;

    private Invoker invoker;

    private CacheInvokeConfig cacheInvokeConfig;

}
