package me.about.widget.routing;

import com.google.common.collect.Lists;
import me.about.widget.routing.exception.DatabaseNotFoundException;

import java.util.List;

/**
 * 分库上下文
 *
 * @author: hugo.zxh
 * @date: 2023/10/16 23:48
 */
public class RoutingContext {

    private static final ThreadLocal<String> ROUTING_DATABASE = new ThreadLocal<>();

    private static final List<String> DATABASE_IDS = Lists.newArrayList();

    private static final List<String> SHARDING_COLUMNS = Lists.newArrayList();

    private static final List<String> BROADCAST_TABLES = Lists.newArrayList();


    public static void addDatabaseId(String databaseId) {
        DATABASE_IDS.add(databaseId);
    }
    public static List<String> getDatabaseIds() {
        return DATABASE_IDS;
    }


    public static void addBroadcastTable(String broadcastTable) {
        BROADCAST_TABLES.add(broadcastTable);
    }
    public static List<String> getBroadcastTables() {
        return BROADCAST_TABLES;
    }
    public static void addBroadcastTables(List<String> broadcastTables) {
        BROADCAST_TABLES.addAll(broadcastTables);
    }


    public static List<String> getShardingColumns() {
        return SHARDING_COLUMNS;
    }
    public static void addShardingColumn(String shardingColumn) {
        SHARDING_COLUMNS.add(shardingColumn);
    }
    public static void addShardingColumns(List<String> shardingColumns) {
        SHARDING_COLUMNS.addAll(shardingColumns);
    }

    public static String getRoutingDatabase() {
        String currentDatabase = ROUTING_DATABASE.get();
        if (currentDatabase != null && !DATABASE_IDS.contains(currentDatabase)) {
            throw new DatabaseNotFoundException("{" + currentDatabase + "}, not found");
        }
        return currentDatabase;
    }
    public static void setRoutingDatabase(String shardingDatabase) {
        ROUTING_DATABASE.set(shardingDatabase);
    }

    public static void clear() {
        RoutingContext.ROUTING_DATABASE.remove();
    }

}
