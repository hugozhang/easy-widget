package me.about.widget.cache.spring.boot.autoconfig;

import me.about.widget.cache.core.aop.CachedAspect;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * spring boot autoconfig
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 21:40
 * @description:
 */
public class BeanHub implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {
                MultiCacheConfig.class.getName(),
                CachedAspect.class.getName()
        };
    }
}