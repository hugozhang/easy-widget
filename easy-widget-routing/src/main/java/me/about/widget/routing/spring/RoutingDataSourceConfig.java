package me.about.widget.routing.spring;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

/**
 * 数据源配置加载并创建数据源
 *
 * @author: hugo.zxh
 * @date: 2023/10/17 9:40
 */

@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Configuration
public class RoutingDataSourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingDataSourceConfig.class);

    @Resource
    private RoutingDataSourceProperties routingDataSourceProperties;

    private DataSource initShardingDataSource() {
        Map<String, Map<?, ?>> databases = routingDataSourceProperties.getDatabases();
        Preconditions.checkArgument(!CollectionUtils.isEmpty(databases), "sharding.databases config is'not found.");

        Map<Object, Object> targetDataSources = Maps.newHashMap();
        DataSource defaultTargetDataSource = null;

        Set<Map.Entry<String, Map<?, ?>>> entries = databases.entrySet();
        for (Map.Entry<String, Map<?, ?>> entry : entries) {
            String databaseId = entry.getKey();
            Map<?, ?> properties = entry.getValue();
            DataSource dataSource = createDataSource(properties);
            if (dataSource != null) {
                targetDataSources.put(databaseId, dataSource);
                if (defaultTargetDataSource == null) {
                    defaultTargetDataSource = dataSource;
                }
            }
        }

        Preconditions.checkArgument(!CollectionUtils.isEmpty(targetDataSources), "sharding.databases config is'not found.");

        Preconditions.checkNotNull(defaultTargetDataSource, "defaultTargetDataSource is required.");

        RoutingDataSource shardingDataSource = new RoutingDataSource();
        shardingDataSource.setTargetDataSources(targetDataSources);
        shardingDataSource.setDefaultTargetDataSource(defaultTargetDataSource);
        shardingDataSource.setLenientFallback(false);
        shardingDataSource.afterPropertiesSet();
        return shardingDataSource;
    }

    private DataSource createDataSource(Map properties) {
        DataSource dataSource = null;
        try {
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        }
        return dataSource;
    }

    @Bean
    public DataSource shardingDataSource() {
        LazyConnectionDataSourceProxy dataSourceProxy = new LazyConnectionDataSourceProxy();
        dataSourceProxy.setTargetDataSource(initShardingDataSource());
        return dataSourceProxy;
    }
}
