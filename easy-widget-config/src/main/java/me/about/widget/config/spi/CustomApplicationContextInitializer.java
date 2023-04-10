package me.about.widget.config.spi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 容器初始化前
 *
 * @author: hugo.zxh
 * @date: 2023/02/03 13:54
 * @description:
 */
@Slf4j
public class CustomApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final List<PropertySourceLocator> propertySourceLocators;

    public CustomApplicationContextInitializer() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        propertySourceLocators = new ArrayList(SpringFactoriesLoader.loadFactories(PropertySourceLocator.class,classLoader));
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        for(PropertySourceLocator locator : this.propertySourceLocators) {
            Collection<PropertySource<?>> sources = locator.locateCollection(environment,applicationContext);
            if(sources == null|| sources.size() == 0) {
                continue;
            }
            for (PropertySource<?> p:sources) {
                mutablePropertySources.addLast(p);
            }
        }
    }
}
