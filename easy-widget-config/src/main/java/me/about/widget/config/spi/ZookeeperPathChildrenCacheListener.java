package me.about.widget.config.spi;

import me.about.widget.config.spring.property.PlaceholderHelper;
import me.about.widget.config.spring.property.SpringValue;
import me.about.widget.config.spring.property.SpringValueRefresh;
import me.about.widget.config.spring.property.SpringValueRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

/**
 * ZookeeperPathChildrenCacheListener
 *
 * @author: hugo.zxh
 * @date: 2023/02/03 22:06
 * @description:
 */
public class ZookeeperPathChildrenCacheListener implements PathChildrenCacheListener {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperPathChildrenCacheListener.class);

    private boolean typeConverterHasConvertIfNecessaryWithFieldParameter;

    private Environment environment;

    private ConfigurableBeanFactory beanFactory;

    private TypeConverter typeConverter;

    private SpringValueRegistry springValueRegistry;

    private PlaceholderHelper placeholderHelper;

    public ZookeeperPathChildrenCacheListener(Environment environment, ConfigurableApplicationContext applicationContext) {
        this.typeConverterHasConvertIfNecessaryWithFieldParameter = testTypeConverterHasConvertIfNecessaryWithFieldParameter();

        this.environment = environment;
        this.beanFactory = applicationContext.getBeanFactory();

        this.typeConverter = beanFactory.getTypeConverter();

        this.placeholderHelper = PlaceholderHelper.getInstance();
        this.springValueRegistry = SpringValueRegistry.getInstance();
    }

    private boolean testTypeConverterHasConvertIfNecessaryWithFieldParameter() {
        try {
            TypeConverter.class.getMethod("convertIfNecessary", Object.class, Class.class, Field.class);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(),ex);
            return false;
        }
        return true;
    }


    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) {
        if (event.getData() == null) {
            return;
        }
        String path = event.getData().getPath();
        String key = SpringValueRefresh.getKey(environment,path);
        String value = new String(event.getData().getData(), StandardCharsets.UTF_8);
        if (key == null) {
            return;
        }
        if (event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {
            logger.info("childNode initialized successfully ");
        }
        if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
            logger.info("childNode data add successfully, path => {}, data => {}",
                    path,
                    value);
            updateEnvironment(key,value);
            fireConfigValueChange(key);
        }
        if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
            logger.info("childNode data remove successfully, path => {}, data => {}",
                    path,
                    value);
        }
        if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
            logger.info("childNode data update successfully, path => {}, data => {}",
                    path,
                    value);
            updateEnvironment(key,value);
            fireConfigValueChange(key);
        }
    }


    private void updateEnvironment(String key, String value) {
        ((ConfigurableEnvironment)this.environment).getPropertySources().stream().forEach( x -> {
            if (x instanceof CompositePropertySource) {
                ((CompositePropertySource)x).getPropertySources().stream().forEach( e -> {
                    if (e instanceof MapPropertySource) {
                        Map<String, Object> source = ((MapPropertySource) e).getSource();
                        source.put(key,value);
                    }
                });
            }
        });
    }

    private void fireConfigValueChange(String key) {
        Collection<SpringValue> springValues = springValueRegistry.get(beanFactory, key);
        if (springValues == null || springValues.isEmpty()) {
            return;
        }
        for (SpringValue val : springValues) {
            updateSpringValue(val);
        }
    }

    private void updateSpringValue(SpringValue springValue) {
        try {
            Object value = resolvePropertyValue(springValue);
            springValue.update(value);
        } catch (Throwable ex) {
            logger.error("Auto update changed value failed, {}", springValue.toString(), ex);
        }
    }

    /**
     * Logic transplanted from DefaultListableBeanFactory
     *
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency(org.springframework.beans.factory.config.DependencyDescriptor,String, java.util.Set, TypeConverter)
     */
    private Object resolvePropertyValue(SpringValue springValue) {
        // value will never be null, as @Value and @ApolloJsonValue will not allow that
        Object value = this.placeholderHelper.resolvePropertyValue(beanFactory,springValue.getBeanName(),springValue.getPlaceholder());
        if (typeConverterHasConvertIfNecessaryWithFieldParameter) {
            value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType(), springValue.getField());
        } else {
            value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType());
        }
        return value;
    }
}
