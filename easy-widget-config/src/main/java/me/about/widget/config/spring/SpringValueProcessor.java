package me.about.widget.config.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * SpringValueProcessor
 *
 * @author: hugo.zxh
 * @date: 2023/02/03 22:30
 * @description:
 */
@Component
public class SpringValueProcessor implements ApplicationContextAware,BeanPostProcessor, PriorityOrdered {

    private static final Logger logger = LoggerFactory.getLogger(SpringValueProcessor.class);

    private final SpringValueRegistry springValueRegistry;

    private final PlaceholderHelper placeholderHelper;

    private ConfigurableBeanFactory beanFactory;

    public SpringValueProcessor() {
        this.springValueRegistry = SpringValueRegistry.getInstance();
        this.placeholderHelper = PlaceholderHelper.getInstance();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        for (Field field : findAllField(clazz)) {
            processField(bean, beanName, field);
        }
        return bean;
    }

    private void processField(Object bean, String beanName, Field field) {
        // 查找有 @Value 注释的字段
        Value value = field.getAnnotation(Value.class);
        if (value == null) {
            return;
        }
        doRegister(bean, beanName, field, value);
    }

    private void doRegister(Object bean, String beanName, Field field, Value value) {
        Set<String> keys = placeholderHelper.extractPlaceholderKeys(value.value());
        if (keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            SpringValue springValue = new SpringValue(key, value.value(), bean, beanName, field);
            springValueRegistry.register(beanFactory,key, springValue);
            logger.info("Monitoring {}", springValue);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private List<Field> findAllField(Class<?> clazz) {
        final List<Field> res = new LinkedList<>();
        ReflectionUtils.doWithFields(clazz, res::add);
        return res;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
    }
}

