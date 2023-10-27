package me.about.widget.config.spi;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Collection;
import java.util.Collections;

/**
 * 资源加载器
 *
 * @author: hugo.zxh
 * @date: 2023/02/03 13:57
 * @description:
 */
public interface PropertySourceLocator {

    /**
     * 资源
     * @param environment
     * @param applicationContext
     * @return
     */
    PropertySource<?> locate(Environment environment, ConfigurableApplicationContext applicationContext);

    /**
     * 资源
     * @param environment
     * @param applicationContext
     * @return
     */
    default Collection<PropertySource<?>> locateCollection(Environment environment, ConfigurableApplicationContext applicationContext){
        return locateCollections(this,environment,applicationContext);
    }

    /**
     * 资源
     * @param locator
     * @param environment
     * @param applicationContext
     * @return
     */
    default Collection<PropertySource<?>> locateCollections(PropertySourceLocator locator, Environment environment, ConfigurableApplicationContext applicationContext) {
        PropertySource<?> propertySource = locator.locate(environment,applicationContext);
        return propertySource == null ? Collections.emptyList() : Collections.singletonList(propertySource);
    }

}
