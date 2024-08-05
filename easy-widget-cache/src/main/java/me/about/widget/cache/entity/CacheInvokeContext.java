package me.about.widget.cache.entity;

import lombok.Data;
import me.about.widget.cache.core.Invoker;

import java.lang.reflect.Method;

@Data
public class CacheInvokeContext {

    private Method method;

    private Object[] args;

    private Class<?> returnType;

    private Invoker invoker;

    private CacheInvokeConfig cacheInvokeConfig;

}
