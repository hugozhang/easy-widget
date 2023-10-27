package me.about.widget.routing;

/**
 * 分库上下文
 *
 * @author: hugo.zxh
 * @date: 2023/10/16 23:48
 */
public class RoutingContext {

    private static final ThreadLocal<String> SHARDING_DATABASE = new ThreadLocal<>();

    public static String getShardingDatabase() {
        return SHARDING_DATABASE.get();
    }

    public static void setShardingDatabase(String shardingDatabase) {
        SHARDING_DATABASE.set(shardingDatabase);
    }

    public static void clear() {
        RoutingContext.SHARDING_DATABASE.remove();
    }

}
