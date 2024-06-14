package me.about.widget.otel.zipkin.spring;


import brave.Span;
import brave.Span.Kind;
import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ThreadLocalSpan;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListenerProvider;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import zipkin2.Endpoint;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;

@ConditionalOnBean({Tracing.class})
@ConditionalOnProperty(
        name = {"zipkin.storage.type"},
        havingValue = "mysql"
)
@Configuration
public class TracingZipkinMySQLStorageAutoConfiguration extends DefaultExecuteListener {

    @Autowired(required = false)
    ZipkinMySQLStorageProperties mysql;

    @Autowired
    @Qualifier("mysql")
    Endpoint mysqlEndpoint;

    @Autowired
    @Qualifier("mysql")
    ThreadLocalSpan threadLocalSpan;

    @Autowired(required = false)
    CurrentTraceContext currentTraceContext;

    public TracingZipkinMySQLStorageAutoConfiguration() {
    }

    @Bean
    ExecuteListenerProvider tracingExecuteListenerProvider() {
        return new DefaultExecuteListenerProvider(this);
    }

    @Bean
    @ConditionalOnMissingBean({Executor.class})
    public Executor executor(CurrentTraceContext currentTraceContext) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("MySQLStorage-");
        executor.initialize();
        return currentTraceContext.executor(executor);
    }

    @Bean
    @Qualifier("mysql")
    Endpoint mysql() throws UnknownHostException {
        Endpoint.Builder builder = Endpoint.newBuilder().serviceName("mysql");
        builder.parseIp(InetAddress.getByName(this.mysql.getHost()));
        return builder.port(this.mysql.getPort()).build();
    }

    @Bean
    @Qualifier("mysql")
    ThreadLocalSpan mysqlThreadLocalSpan(Tracing tracing) {
        return ThreadLocalSpan.create(tracing.tracer());
    }

    public void renderEnd(ExecuteContext ctx) {
        if (this.currentTraceContext.get() != null) {
            Span span = this.threadLocalSpan.next();
            if (span != null && !span.isNoop()) {
                String sql = ctx.sql();
                int spaceIndex = sql.indexOf(32);
                span.kind(Kind.CLIENT).name(spaceIndex == -1 ? sql : sql.substring(0, spaceIndex));
                span.tag("sql.query", sql);
                span.remoteEndpoint(this.mysqlEndpoint);
                span.start();
            }
        }
    }

    public void executeEnd(ExecuteContext ctx) {
        Span span = ThreadLocalSpan.CURRENT_TRACER.remove();
        if (span != null && !span.isNoop()) {
            if (ctx.sqlException() != null) {
                span.tag("error", Integer.toString(ctx.sqlException().getErrorCode()));
            }

            span.finish();
        }
    }
}
