package me.about.widget.jobflow.spring.boot.autoconfig;

import me.about.widget.jobflow.engine.JobFlowContext;
import me.about.widget.jobflow.engine.JobFlowEngine;
import me.about.widget.jobflow.engine.JobFlowExecutor;
import me.about.widget.jobflow.engine.JobFlowRegistrar;
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
                JobFlowEngine.class.getName(),
                JobFlowContext.class.getName(),
                JobFlowRegistrar.class.getName(),
                JobFlowExecutor.class.getName()
        };
    }
}