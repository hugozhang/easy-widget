package me.about.widget.routing.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 分库配置
 *
 * @author: hugo.zxh
 * @date: 2023/10/16 23:47
 */
@Configuration
@ConfigurationProperties(prefix = "sharding")
@Data
public class RoutingDataSourceProperties {

    /**
     * 配置举例
     *
     * sharding:
     *   databases:
     *     db1:
     *       username: 11
     *       password: 11
     * 或
     * sharding.databases.db2.username = 22
     * sharding.databases.db2.password = 22
     */
    private Map<String, Map<?,?>> databases;

}
