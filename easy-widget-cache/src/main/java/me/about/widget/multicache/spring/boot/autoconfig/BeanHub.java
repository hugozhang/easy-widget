package me.about.widget.multicache.spring.boot.autoconfig;

import me.about.widget.multicache.aop.MultiCacheAspect;
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
                MultiCacheAspect.class.getName()
        };
    }
}