package me.about.widget.mybatis.plugin.page.spring;

import me.about.widget.mybatis.plugin.page.model.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.support.AopUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 分页代理
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 9:35
 */
public class PageMapperProxy <T> implements InvocationHandler, Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(PageMapperProxy.class);

    private Object mapper;

    public PageMapperProxy(Object mapper) {
        this.mapper = mapper;
    }

    private boolean isScopedObjectGetTargetObject(Method method) {
        return method.getDeclaringClass().equals(ScopedObject.class)
                && method.getName().equals("getTargetObject")
                && method.getParameterTypes().length == 0;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (AopUtils.isEqualsMethod(method) || AopUtils.isToStringMethod(method)
                    || AopUtils.isHashCodeMethod(method)
                    || isScopedObjectGetTargetObject(method)
                    || !PageResult.class.isAssignableFrom(method.getReturnType())) {
                return method.invoke(mapper, args);
            }

            LOGGER.debug("invoke proxy={} method={} args={}", proxy, method, args);

            Object result = method.invoke(mapper, args);
            if ((method.getReturnType().equals(Void.TYPE))) {
                return result;
            }

            InternalResult internalResult = InternalResultContext.getResult();
            PageResult pageResult = new PageResult();
            pageResult.setTotal(internalResult.getTotal());
            pageResult.setTotalPage(internalResult.getTotalPage());
            pageResult.setRows(internalResult.getRows());
            return pageResult;
        } finally {
            InternalResultContext.clear();
        }
    }
}
