package me.about.widget.otel.zipkin.spring.boot.autoconfig;

import me.about.widget.otel.zipkin.spring.*;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * spring boot autoconfig
 *
 * @author: hugo.zxh
 * @date: 2024/06/14 16:40
 * @description:
 */
public class BeanHub implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {
                InternalZipkinConfiguration.class.getName(),
                OpenTelemetrySamplerConfiguration.class.getName(),
                TracingZipkinMySQLStorageAutoConfiguration.class.getName(),
                ZipkinMySQLStorageAutoConfiguration.class.getName(),
                ZipkinMySQLStorageProperties.class.getName(),
                ZipkinServerConfiguration.class.getName(),
                ZipkinUiProperties.class.getName()
        };
    }
}