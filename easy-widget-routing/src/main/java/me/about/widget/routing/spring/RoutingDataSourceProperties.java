package me.about.widget.routing.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 分库配置
 *
 * @author: hugo.zxh
 * @date: 2023/10/16 23:47
 */
@Configuration
@ConfigurationProperties(prefix = "routing")
@Data
public class RoutingDataSourceProperties {

    /**
     * 配置举例
     *
     * routing:
     *  rules:
     *    shardingColumns:
     *      - medins_no
     *      - yljgdm
     *    broadcastTables:
     *      - a
     *      - b
     *      - c
     *  databases:
     *    db1:
     *      username: 11
     *      password: 11
     * 或
     * sharding.databases.db2.username = 22
     * sharding.databases.db2.password = 22
     */
    private Map<String, Map<String,Object>> databases;

    private Map<String,Object> databaseProperties;

    private RoutingRules rules;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoutingRules {

        private List<String> shardingColumns;

        private List<String> broadcastTables;
    }

}
