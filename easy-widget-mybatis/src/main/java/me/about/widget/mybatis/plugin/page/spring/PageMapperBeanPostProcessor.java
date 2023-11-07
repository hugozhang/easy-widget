package me.about.widget.mybatis.plugin.page.spring;

import com.google.common.collect.Sets;
import me.about.widget.mybatis.plugin.page.model.PageResult;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * page mapper 处理
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 9:21
 */
@Configuration
@ConditionalOnClass(MapperFactoryBean.class)
@AutoConfigureBefore(name="org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PageMapperBeanPostProcessor implements BeanPostProcessor, EmbeddedValueResolverAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(PageMapperBeanPostProcessor.class);

    private StringValueResolver resolver;

    private Set<Class<?>> mapperClasps = Sets.newHashSet();

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    private static boolean isMapper(Class<?> mapperClass) {
        Method[] methods = mapperClass.getMethods();
        for(Method method : methods) {
            if (PageResult.class.isAssignableFrom(method.getReturnType())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MapperFactoryBean) {
            MapperFactoryBean mapperFactoryBean = (MapperFactoryBean) bean;
            Class<?> mapperInterface = mapperFactoryBean.getMapperInterface();
            if(isMapper(mapperInterface)) {
                mapperClasps.add(mapperInterface);
                LOGGER.info("find MapperFactoryBean beanName={} mapperInterface={}",beanName,mapperInterface);
            }
        } else {
            for(Class<?> mapperClass : mapperClasps) {
                if(bean.getClass().equals(mapperClass)) {
                    LOGGER.info("proxy Mapper beanName={} class={} ",beanName,mapperClass);
                    return createMapperProxy(bean, mapperClass);
                } else {
                    for(Class<?> interfaceClass : bean.getClass().getInterfaces()){
                        if(interfaceClass.equals(mapperClass)) {
                            LOGGER.info("proxy Mapper beanName={} class={} ",beanName,mapperClass);
                            return createMapperProxy(bean, mapperClass);
                        }
                    }
                }
            }
        }
        return bean;
    }

    private Object createMapperProxy(Object bean, Class<?> mapperClass) {
        return Proxy.newProxyInstance(mapperClass.getClassLoader(),new Class[]{ mapperClass },new PageMapperProxy<>(mapperClass,bean,resolver));
    }
}
