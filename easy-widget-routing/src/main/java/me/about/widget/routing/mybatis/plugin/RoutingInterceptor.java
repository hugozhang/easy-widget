package me.about.widget.routing.mybatis.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2023/10/18 20:34
 */
@Intercepts({
    @Signature(type=StatementHandler.class,method="query",args={Statement.class, ResultHandler.class}),
    @Signature(type=StatementHandler.class,method="update",args={Statement.class})
})
public class RoutingInterceptor implements Interceptor {
    private final static Logger logger = LoggerFactory.getLogger(RoutingInterceptor.class);
    private static final String DELEGATE_BOUND_SQL_SQL = "delegate.boundSql.sql";
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        String sql = (String) metaObject.getValue(DELEGATE_BOUND_SQL_SQL);
        logger.info("SQL : {}.",sql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private Object realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return target;
    }
}
