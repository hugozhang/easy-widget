package me.about.widget.routing.spring;

import me.about.widget.routing.RoutingContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

/**
 * 数据源路由
 *
 * @author: hugo.zxh
 * @date: 2023/10/17 0:03
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return RoutingContext.getRoutingDatabase();
    }

    public DataSource getActualDataSource() {
        Object lookupKey = determineCurrentLookupKey();
        if(null == lookupKey) {
            return this;
        }
        DataSource determineTargetDataSource = this.determineTargetDataSource();
        return determineTargetDataSource == null ? this : determineTargetDataSource;
    }
}
