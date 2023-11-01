package me.about.widget.routing.spring;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import javax.sql.DataSource;

/**
 * 多数据源事务管理工厂
 *
 * @author: hugo.zxh
 * @date: 2022/05/20 11:32
 * @description:
 */
public class RoutingDataSourceTransactionFactory extends SpringManagedTransactionFactory {
    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new RoutingDataSourceTransaction(dataSource);
    }
}
