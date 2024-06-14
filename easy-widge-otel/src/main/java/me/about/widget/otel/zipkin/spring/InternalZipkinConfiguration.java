package me.about.widget.otel.zipkin.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import zipkin2.server.internal.ZipkinHttpCollector;
import zipkin2.server.internal.ZipkinQueryApiV2;
import zipkin2.server.internal.brave.TracingConfiguration;

@Configuration
@Import({ZipkinServerConfiguration.class, TracingConfiguration.class, ZipkinQueryApiV2.class, ZipkinHttpCollector.class})
public class InternalZipkinConfiguration {
    public InternalZipkinConfiguration() {
    }
}
