package me.about.widget.config.spring.property;


import me.about.widget.config.ConfigChangeListener;
import me.about.widget.config.model.ConfigChange;
import me.about.widget.config.model.ConfigChangeEvent;
import me.about.widget.config.refresh.RefreshBeanEvent;
import me.about.widget.config.refresh.constant.RefreshBeanConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 配置变更监听
 *
 * @author: hugo.zxh
 * @date: 2023/10/20 14:35
 */
public class AutoUpdateConfigChangeListener implements ConfigChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(AutoUpdateConfigChangeListener.class);

    private final boolean typeConverterHasConvertIfNecessaryWithFieldParameter;
    private final ConfigurableEnvironment environment;
    private final ConfigurableBeanFactory beanFactory;
    private final TypeConverter typeConverter;
    private final PlaceholderHelper placeholderHelper;
    private final SpringValueRegistry springValueRegistry;
    private final ConfigurableApplicationContext applicationContext;

    public AutoUpdateConfigChangeListener(ConfigurableApplicationContext applicationContext){
        this.typeConverterHasConvertIfNecessaryWithFieldParameter = testTypeConverterHasConvertIfNecessaryWithFieldParameter();
        this.beanFactory = applicationContext.getBeanFactory();
        this.typeConverter = this.beanFactory.getTypeConverter();
        this.environment = applicationContext.getEnvironment();
        this.placeholderHelper = PlaceholderHelper.getInstance();
        this.springValueRegistry = SpringValueRegistry.getInstance();
        this.applicationContext = applicationContext;
    }

    private void refreshEnvironment(ConfigChangeEvent changeEvent) {
        // refresh environment
        Map<String, Object> changeMap = changeEvent.getChanges().values()
                .stream()
                .collect(Collectors.toMap(ConfigChange::getPropertyName, ConfigChange::getNewValue, (k1, k2) -> k1));

        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        if (propertySources.contains(RefreshBeanConstant.SCOPE_NAME)) {
            PropertySource<?> x = propertySources.get(RefreshBeanConstant.SCOPE_NAME);
            if (x instanceof MapPropertySource) {
                MapPropertySource propertySource = (MapPropertySource) x;
                Map<String, Object> source = propertySource.getSource();
                source.putAll(changeMap);
            }
        } else {
            MapPropertySource refresh = new MapPropertySource(RefreshBeanConstant.SCOPE_NAME, changeMap);
            propertySources.addFirst(refresh);
        }
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        Set<String> keys = changeEvent.changedKeys();
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        refreshEnvironment(changeEvent);

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

        // 1. refresh bean
        applicationContext.publishEvent(new RefreshBeanEvent(this,keys));
    }

    private void updateSpringValue(SpringValue springValue) {
        try {
            Object value = resolvePropertyValue(springValue);
            springValue.update(value);

            logger.info("Auto update changed value successfully, new value: {}, {}", value,
                    springValue);
        } catch (Throwable ex) {
            logger.error("Auto update changed value failed, {}", springValue.toString(), ex);
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
