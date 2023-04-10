package me.about.widget.mybatis.sensitive.spring;

import me.about.widget.mybatis.sensitive.annotation.SensitiveMapper;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * 基于mybatis mapper的敏感数据处理
 *
 * @author: hugo.zxh
 * @date: 2023/03/21 14:19
 */
public class SensitiveMapperBeanPostProcessor implements BeanPostProcessor, EmbeddedValueResolverAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(SensitiveMapperBeanPostProcessor.class);

    private Set<Class<?>> mapperClasps = new HashSet<>();

    private StringValueResolver resolver;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private static boolean isSensitiveMapper(Class<?> mapperClass){
        if(mapperClass.getAnnotation(SensitiveMapper.class) != null){
            return true;
        }
        for(Class<?> t : mapperClass.getInterfaces()){
            return isSensitiveMapper(t);
        }
        return false;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MapperFactoryBean) {
            MapperFactoryBean mapperFactoryBean = (MapperFactoryBean) bean;
            Class<?> mapperInterface = mapperFactoryBean.getMapperInterface();
            if(isSensitiveMapper(mapperInterface)) {
                mapperClasps.add(mapperInterface);
                LOGGER.debug("find MapperFactoryBean beanName={} mapperInterface={}",beanName,mapperInterface);
            }
        } else {
            for(Class<?> mapperClass : mapperClasps) {
                if(bean.getClass().equals(mapperClass)) {
                    LOGGER.debug("proxy Mapper beanName={} class={} ",beanName,mapperClass);
                    return createMapperProxy(bean, mapperClass);
                } else {
                    for(Class<?> interfaceClass : bean.getClass().getInterfaces()){
                        if(interfaceClass.equals(mapperClass)) {
                            LOGGER.debug("proxy Mapper beanName={} class={} ",beanName,mapperClass);
                            return createMapperProxy(bean, mapperClass);
                        }
                    }
                }
            }
        }
        return bean;
    }

    private Object createMapperProxy(Object bean, Class<?> mapperClass) {
        return Proxy.newProxyInstance(mapperClass.getClassLoader(),new Class[]{ mapperClass },new SensitiveMapperProxy<>(mapperClass,bean,resolver));
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }
}

