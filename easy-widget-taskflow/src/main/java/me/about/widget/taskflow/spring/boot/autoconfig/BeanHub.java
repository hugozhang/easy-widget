package me.about.widget.taskflow.spring.boot.autoconfig;

import me.about.widget.taskflow.spring.JobFlowExecutor;
import me.about.widget.taskflow.spring.JobFlowRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * spring web
 *
 * @author: hugo.zxh
 * @date: 2024/08/09 15:30
 * @description:
 */
public class BeanHub implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {
                JobFlowRegistrar.class.getName(),
                JobFlowExecutor.class.getName()
        };
    }
}