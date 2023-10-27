package me.about.widget.routing.strategy;

/**
 * 分库策略
 *
 * @author: hugo.zxh
 * @date: 2023/10/16 23:40
 */
public interface RoutingDatabaseStrategy {

    /**
     * 分库策略
     * @param shardingKey
     * @return
     */
    String databaseId(String shardingKey);

}
