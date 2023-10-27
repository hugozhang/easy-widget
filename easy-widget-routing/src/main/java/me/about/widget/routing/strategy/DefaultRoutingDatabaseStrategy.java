package me.about.widget.routing.strategy;

/**
 * 分库策略
 *
 * @author: hugo.zxh
 * @date: 2023/10/16 23:44
 */
public class DefaultRoutingDatabaseStrategy implements RoutingDatabaseStrategy {

    @Override
    public String databaseId(String shardingKey) {
        return null;
    }

}
