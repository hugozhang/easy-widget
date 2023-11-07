package me.about.widget.mybatis.plugin.page.spring;

import me.about.widget.mybatis.plugin.page.model.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringValueResolver;

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

    private Class<T> mapperClass;

    private Object mapper;

    private StringValueResolver resolver;

    public PageMapperProxy(Class<T> mapperClass, Object mapper, StringValueResolver resolver) {
        this.mapperClass = mapperClass;
        this.mapper = mapper;
        this.resolver = resolver;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
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
