package me.about.widget.routing.spring;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.about.widget.config.refresh.config.annotation.RefreshBeanScope;
import me.about.widget.routing.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
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

//    @PostConstruct
    public void afterPropertiesSet() {
        RoutingDataSourceProperties.RoutingRules routingRules = routingDataSourceProperties.getRules();
        Preconditions.checkArgument(routingRules != null,"{routing.rules} not found");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(routingRules.getBroadcastTables()),"{routing.rules.broadcastTables} not found");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(routingRules.getShardingColumns()),"{routing.rules.shardingColumns} not found");

        List<String> mergeList = Lists.newArrayList();
        for (String row : routingRules.getBroadcastTables()) {
            List<String> splitToList = Splitter.on(",").splitToList(row);
            mergeList.addAll(splitToList);
        }
        RoutingContext.addBroadcastTables(mergeList);
        RoutingContext.addShardingColumns(routingRules.getShardingColumns());

        Map<String, Map<String, Object>> databases = routingDataSourceProperties.getDatabases();
        Preconditions.checkArgument(!CollectionUtils.isEmpty(databases), "{routing.databases} not found.");
        Map<String, Object> databaseProperties = routingDataSourceProperties.getDatabaseProperties();
        Preconditions.checkArgument(!CollectionUtils.isEmpty(databaseProperties), "{routing.databaseProperties} not found.");

        // merge properties
        for (Map.Entry<String,Object> databasePropertiesEntry : databaseProperties.entrySet()) {
            for (Map.Entry<String, Map<String, Object>> entry : databases.entrySet()) {
                Map<String, Object> value = entry.getValue();
                value.putIfAbsent(databasePropertiesEntry.getKey(),databasePropertiesEntry.getValue());
            }
        }
    }

    private RoutingDataSource initShardingDataSource() {
        Map<String, Map<String, Object>> databases = routingDataSourceProperties.getDatabases();
        Map<Object, Object> targetDataSources = Maps.newHashMap();
        DataSource defaultTargetDataSource = null;

        Set<Map.Entry<String, Map<String, Object>>> entries = databases.entrySet();
        for (Map.Entry<String, Map<String, Object>> entry : entries) {
            String databaseId = entry.getKey();
            RoutingContext.addDatabaseId(databaseId);

            Map<?, ?> properties = entry.getValue();
            DataSource dataSource = createDataSource(properties);
            if (dataSource != null) {
                targetDataSources.put(databaseId, dataSource);
                if (defaultTargetDataSource == null) {
                    defaultTargetDataSource = dataSource;
                }
            }
        }

        Preconditions.checkArgument(!CollectionUtils.isEmpty(targetDataSources), "targetDataSources is required.");
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
    @RefreshBeanScope
    public RoutingDataSource routingDataSource() {
//        LazyConnectionDataSourceProxy dataSourceProxy = new LazyConnectionDataSourceProxy();
//        dataSourceProxy.setTargetDataSource(initShardingDataSource());
//        return dataSourceProxy;
        afterPropertiesSet();
        return initShardingDataSource();
    }
}
