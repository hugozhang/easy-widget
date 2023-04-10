package me.about.widget.config.spring;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.springframework.beans.factory.BeanFactory;

import java.util.Collection;
import java.util.Map;

/**
 * SpringValueRegistry
 *
 * @author: hugo.zxh
 * @date: 2023/02/03 22:26
 * @description:
 */
public class SpringValueRegistry {

    private static final SpringValueRegistry INSTANCE = new SpringValueRegistry();

    private final Map<BeanFactory,Multimap<String, SpringValue>> registry = Maps.newConcurrentMap();

    private static final Object LOCK = new Object();

    private SpringValueRegistry() {
    }

    public static SpringValueRegistry getInstance() {
        return INSTANCE;
    }

    public void register(BeanFactory beanFactory, String key, SpringValue springValue) {
        if (!registry.containsKey(beanFactory)) {
            synchronized (LOCK) {
                if (!registry.containsKey(beanFactory)) {
                    registry.put(beanFactory, Multimaps.synchronizedListMultimap(LinkedListMultimap.create()));
                }
            }
        }
        registry.get(beanFactory).put(key, springValue);
    }

    public Collection<SpringValue> get(BeanFactory beanFactory, String key) {
        Multimap<String, SpringValue> beanFactorySpringValues = registry.get(beanFactory);
        if (beanFactorySpringValues == null) {
            return null;
        }
        return beanFactorySpringValues.get(key);
    }
}

