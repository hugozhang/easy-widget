package me.about.widget.otel.zipkin.sampler;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingDecision;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpanFilterSampler implements Sampler {

    private static final List<String> EXCLUDED_SPAN_NAMES = Collections.unmodifiableList(
            Arrays.asList("insert zipkin.zipkin_spans", "spanName2")
    );


    private static final List<String> EXCLUDED_HTTP_REQUEST_TARGETS = Collections.unmodifiableList(
            Arrays.asList("/api/v1/span", "/health/checks")
    );

    @Override
    public SamplingResult shouldSample(Context parentContext, String traceId, String name, SpanKind spanKind, Attributes attributes, List<LinkData> parentLinks) {
//        String httpTarget = attributes.get(SemanticAttributes.HTTP_TARGET) != null ? attributes.get(SemanticAttributes.HTTP_TARGET) : "";
        if (name.contains("zipkin")) { // 根据条件进行过滤
            return SamplingResult.create(SamplingDecision.DROP);
        } else {
            return SamplingResult.create(SamplingDecision.RECORD_AND_SAMPLE);
        }
    }

    @Override
    public String getDescription() {
        return "SpanFilterSampler"; // SpanFilterSampler可以替换为自定义的名称
    }
}
