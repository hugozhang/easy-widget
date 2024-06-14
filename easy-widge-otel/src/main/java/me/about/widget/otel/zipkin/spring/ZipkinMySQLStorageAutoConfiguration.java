package me.about.widget.otel.zipkin.spring;


import org.jooq.ExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import zipkin2.storage.StorageComponent;
import zipkin2.storage.mysql.v1.MySQLStorage;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@ConditionalOnProperty(
        name = {"zipkin.storage.type"},
        havingValue = "mysql"
)
@ConditionalOnMissingBean({StorageComponent.class})
public class ZipkinMySQLStorageAutoConfiguration {

    @Autowired(required = false)
    ZipkinMySQLStorageProperties mysql;

    @Autowired(required = false)
    @Qualifier("tracingExecuteListenerProvider")
    ExecuteListenerProvider listener;

    ZipkinMySQLStorageAutoConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean({Executor.class})
    Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("ZipkinMySQLStorage-");
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean({DataSource.class})
    DataSource mysqlDataSource() {
        return this.mysql.toDataSource();
    }

    @Bean
    StorageComponent storage(Executor executor, DataSource dataSource, @Value("${zipkin.storage.strict-trace-id:true}") boolean strictTraceId, @Value("${zipkin.storage.search-enabled:true}") boolean searchEnabled, @Value("${zipkin.storage.autocomplete-keys:}") List<String> autocompleteKeys) {
        return MySQLStorage.newBuilder().strictTraceId(strictTraceId).searchEnabled(searchEnabled).autocompleteKeys(autocompleteKeys).executor(executor).datasource(dataSource).listenerProvider(this.listener).build();
    }
}
