package me.about.widget.routing.spring;

import me.about.widget.routing.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

/**
 * 数据源路由
 *
 * @author: hugo.zxh
 * @date: 2023/10/17 0:03
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    private Logger logger = LoggerFactory.getLogger(RoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        String routingDatabase = RoutingContext.getRoutingDatabase();
        logger.info("current database is {}.",routingDatabase);
        return routingDatabase;
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
