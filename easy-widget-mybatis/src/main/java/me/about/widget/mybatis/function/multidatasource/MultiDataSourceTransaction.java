package me.about.widget.mybatis.function.multidatasource;

import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.util.Assert.notNull;

/**
 * 多数据源事务管理重写
 *
 * @author: hugo.zxh
 * @date: 2022/05/20 11:28
 * @description:
 */
public class MultiDataSourceTransaction implements Transaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiDataSourceTransaction.class);

    private final DynamicDataSource dataSource;

    /**
     * 默认源的链接
     */
    private Connection mainConnection;

    private String mainDatabaseIdentification;

    private ConcurrentMap<String, Connection> otherConnectionMap = new ConcurrentHashMap<>();

    private boolean isConnectionTransactional;

    private boolean autoCommit = false;


    public MultiDataSourceTransaction(DataSource dataSource) {
        notNull(dataSource, "No DataSource specified");
        this.dataSource = (DynamicDataSource) dataSource;
        this.mainDatabaseIdentification= DbContextHolder.getDefaultDbType();
    }

    @Override
    public Connection getConnection() throws SQLException {
        String databaseIdentification = DbContextHolder.getDbType();
        if (databaseIdentification.equals(mainDatabaseIdentification)) {
            if (mainConnection == null) {
                openMainConnection();
                mainDatabaseIdentification = databaseIdentification;
            }
            return mainConnection;
        } else {
            if (!otherConnectionMap.containsKey(databaseIdentification)) {
                /**链接**/
                Connection conn = DataSourceUtils.getConnection(this.dataSource.getActualDataSource());
                conn.setAutoCommit(autoCommit);
                otherConnectionMap.put(databaseIdentification, conn);
            }
            return otherConnectionMap.get(databaseIdentification);
        }

    }

    private void openMainConnection() throws SQLException {
        this.mainConnection = DataSourceUtils.getConnection(this.dataSource);
        this.mainConnection.setAutoCommit(autoCommit);
        this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(this.mainConnection, this.dataSource);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("JDBC Connection ["
                            + this.mainConnection
                            + "] will"
                            + (this.isConnectionTransactional ? " " : " not ")
                            + "be managed by Spring");
        }
    }

    @Override
    public void commit() throws SQLException {
        if (this.mainConnection != null && !this.autoCommit) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Committing JDBC Connection [" + this.mainConnection + "]");
            }
            this.mainConnection.commit();
        }
        if (!otherConnectionMap.isEmpty() && !this.autoCommit) {
            for (Connection connection : otherConnectionMap.values()) {
                connection.commit();
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (this.mainConnection != null && !this.autoCommit) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Rolling back JDBC Connection [" + this.mainConnection + "]");
            }
            this.mainConnection.rollback();
        }
        if (!otherConnectionMap.isEmpty() && !this.autoCommit) {
            for (Connection connection : otherConnectionMap.values()) {
                connection.rollback();
            }
        }
    }

    @Override
    public void close() throws SQLException {
        DataSourceUtils.releaseConnection(this.mainConnection, this.dataSource);
        for (Connection connection : otherConnectionMap.values()) {
            DataSourceUtils.releaseConnection(connection, this.dataSource);
        }
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }
}
