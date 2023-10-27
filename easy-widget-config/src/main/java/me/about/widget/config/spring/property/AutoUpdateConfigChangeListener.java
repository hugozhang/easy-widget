package me.about.widget.config.spring.property;


import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import me.about.widget.config.ConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/10/20 14:35
 */
public class AutoUpdateConfigChangeListener implements ConfigChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(AutoUpdateConfigChangeListener.class);

    private final boolean typeConverterHasConvertIfNecessaryWithFieldParameter;
    private final Environment environment;
    private final ConfigurableBeanFactory beanFactory;
    private final TypeConverter typeConverter;
    private final PlaceholderHelper placeholderHelper;
    private final SpringValueRegistry springValueRegistry;

    public AutoUpdateConfigChangeListener(Environment environment, ConfigurableListableBeanFactory beanFactory){
        this.typeConverterHasConvertIfNecessaryWithFieldParameter = testTypeConverterHasConvertIfNecessaryWithFieldParameter();
        this.beanFactory = beanFactory;
        this.typeConverter = this.beanFactory.getTypeConverter();
        this.environment = environment;
        this.placeholderHelper = PlaceholderHelper.getInstance();
        this.springValueRegistry = SpringValueRegistry.getInstance();
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        Set<String> keys = changeEvent.changedKeys();
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        for (String key : keys) {
            // 1. check whether the changed key is relevant
            Collection<SpringValue> targetValues = springValueRegistry.get(beanFactory, key);
            if (targetValues == null || targetValues.isEmpty()) {
                continue;
            }
            // 2. update the value
            for (SpringValue val : targetValues) {
                updateSpringValue(val);
            }
        }
    }

    private void updateSpringValue(SpringValue springValue) {
        try {
            Object value = resolvePropertyValue(springValue);
            springValue.update(value);

            logger.info("Auto update apollo changed value successfully, new value: {}, {}", value,
                    springValue);
        } catch (Throwable ex) {
            logger.error("Auto update apollo changed value failed, {}", springValue.toString(), ex);
        }
    }

    /**
     * Logic transplanted from DefaultListableBeanFactory
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency(org.springframework.beans.factory.config.DependencyDescriptor, java.lang.String, java.util.Set, org.springframework.beans.TypeConverter)
     */
    private Object resolvePropertyValue(SpringValue springValue) {
        // value will never be null, as @Value and @ApolloJsonValue will not allow that
        Object value = placeholderHelper
                .resolvePropertyValue(beanFactory, springValue.getBeanName(), springValue.getPlaceholder());

        if (springValue.isField()) {
            // org.springframework.beans.TypeConverter#convertIfNecessary(java.lang.Object, java.lang.Class, java.lang.reflect.Field) is available from Spring 3.2.0+
            if (typeConverterHasConvertIfNecessaryWithFieldParameter) {
                value = this.typeConverter
                        .convertIfNecessary(value, springValue.getTargetType(), springValue.getField());
            } else {
                value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType());
            }
        } else {
            value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType(),
                    springValue.getMethodParameter());
        }
        return value;
    }

    private boolean testTypeConverterHasConvertIfNecessaryWithFieldParameter() {
        try {
            TypeConverter.class.getMethod("convertIfNecessary", Object.class, Class.class, Field.class);
        } catch (Throwable ex) {
            return false;
        }
        return true;
    }
}
