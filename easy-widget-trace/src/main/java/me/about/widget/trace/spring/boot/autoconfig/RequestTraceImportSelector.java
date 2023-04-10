package me.about.widget.trace.spring.boot.autoconfig;

import me.about.widget.trace.strategy.aop.RequestTraceAdviceConfig;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 请求链路 import
 *
 * @author: hugo.zxh
 * @date: 2022/02/26 21:40
 * @description:
 */
public class RequestTraceImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {
                RequestTraceAdviceConfig.class.getName()
        };
    }
}