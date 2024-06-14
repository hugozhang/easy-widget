package me.about.widget.otel.zipkin.spring;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import me.about.widget.otel.zipkin.sampler.SpanFilterSampler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetrySamplerConfiguration {

    @Value("${otel.service.name:unknow-service-name}")
    private String serviceName;

    @Value("${otel.exporter.zipkin.endpoint:http://127.0.0.1:9411/api/v2/spans}")
    private String zipkinEndpoint;
    @Bean
    public OpenTelemetrySdk openTelemetrySdk(SdkTracerProvider tracerProvider) {
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();
    }

    @Bean
    public SdkTracerProvider sdkTracerProvider(Sampler spanSampler) {
        AttributesBuilder attributesBuilder = Attributes.builder();
        attributesBuilder.put(AttributeKey.stringKey("service.name"), serviceName);
        Attributes attributes = attributesBuilder.build();
        Resource resource = Resource.create(attributes);

        return SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(ZipkinSpanExporter.builder()
                        .setEndpoint(zipkinEndpoint)
                        .build()).build())
                .setSampler(spanSampler)
                .setResource(resource)
                .build();
    }

    @Bean
    public Sampler spanSampler() {
        return new SpanFilterSampler();
    }
}
