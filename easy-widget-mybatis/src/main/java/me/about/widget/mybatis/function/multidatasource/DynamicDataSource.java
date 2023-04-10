package me.about.widget.mybatis.function.multidatasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;

/**
 * 多数据源切换策略
 *
 * @author: hugo.zxh
 * @date: 2022/05/20 11:20
 * @description:
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);

    public static void setDataSourceKey(DbTypeEnum dbTypeEnum) {
        LOGGER.debug("【多数据源】已切换到{}数据源", dbTypeEnum.getValue());
        DbContextHolder.setDbType(dbTypeEnum);
    }

    /**
     * 取得当前使用哪个数据源
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DbContextHolder.getDbType();
    }

    /**
     * 事务控制的时候用，不然每次都是默认的db1
     * @see TransactionSynchronizationManager#getResource(java.lang.Object)
     * @return
     */
    public DataSource getActualDataSource() {
        Object lookupKey = determineCurrentLookupKey();
        if(null == lookupKey) {
            return this;
        }
        DataSource determineTargetDataSource = this.determineTargetDataSource();
        return determineTargetDataSource==null ? this : determineTargetDataSource;
    }
}
