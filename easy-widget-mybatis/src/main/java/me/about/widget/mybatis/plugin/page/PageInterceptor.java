package me.about.widget.mybatis.plugin.page;

import com.google.common.collect.Lists;
import me.about.widget.mybatis.plugin.page.model.PageParam;
import me.about.widget.mybatis.plugin.page.model.PageResult;
import me.about.widget.mybatis.plugin.page.spring.InternalResult;
import me.about.widget.mybatis.plugin.page.spring.InternalResultContext;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 插件分页
 *
 * @author: hugo.zxh
 * @date: 2023/11/05 12:09
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})

public class PageInterceptor implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Method getMethod(MappedStatement mappedStatement) throws ClassNotFoundException {
        String id = mappedStatement.getId();
        String className = id.substring(0, id.lastIndexOf("."));
//        String methodName = id.substring(id.lastIndexOf(".") + 1);
        final Method[] methods = Class.forName(className).getMethods();
        for (Method method : methods) {
            if (PageResult.class.isAssignableFrom(method.getReturnType())) {
                return method;
            }
        }
        return null;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;
        //由于逻辑关系，只会进入一次
        if (args.length == 4) {
            //4 个参数时
            boundSql = ms.getBoundSql(parameter);
            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
        } else {
            //6 个参数时
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }

        Method method = getMethod(ms);
        if (method == null) {
            return invocation.proceed();
        }

        PageParam<?> pageParam = findPageParameter(boundSql.getParameterObject());
        if (pageParam == null) {
            return invocation.proceed();
        }

        // 方法返回类型
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
        Class<?> resolve = resolvableType.getGeneric(0).resolve();

        // 构建新的ResultMap
        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration()
                , ms.getId(), resolve, Lists.newArrayList())
                .build();

        // 设置ResultMap
        Field resultMapsField = MappedStatement.class.getDeclaredField("resultMaps");
        resultMapsField.setAccessible(true);
        resultMapsField.set(ms,Lists.newArrayList(resultMap));

        int pageSize = pageParam.getPageSize() <= 0 ? 10 : pageParam.getPageSize();
        int currentPage = pageParam.getCurrentPage() <= 0 ? 1 : pageParam.getCurrentPage();
        pageParam.setCurrentPage(currentPage);

        // 生成分页sql
        String sql = boundSql.getSql();
        String pageSql = buildPageSql(sql, pageParam);

        // 设置分页sql
        Field sqlField = BoundSql.class.getDeclaredField("sql");
        sqlField.setAccessible(true);
        sqlField.set(boundSql, pageSql);

        Object proceed = invocation.proceed();

        // 重设分页参数里的总页数等
        long total = getTotal(sql, ms, boundSql);
        int totalPage = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));

        // 设置返回值
        InternalResult<?> internalResult = new InternalResult<>();
        internalResult.setTotal(total);
        internalResult.setTotalPage(totalPage);
        internalResult.setRows((List)proceed);
        InternalResultContext.setResult(internalResult);

        return internalResult;
    }

    private PageParam<?> findPageParameter(Object param) {
        if (param instanceof PageParam<?>) {
            return (PageParam<?>) param;
        } else if (param instanceof Map) {
            for (Object value : ((Map<?, ?>) param).values()) {
                if (value instanceof PageResult<?>) {
                    return (PageParam<?>) value;
                }
            }
        }
        return null;
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    public String buildPageSql(String sql, PageParam<?> pageParam) {
        StringBuilder pageSql = new StringBuilder(sql);
        pageSql.append(" LIMIT " + (pageParam.getCurrentPage() - 1) * pageParam.getPageSize() + "," + pageParam.getPageSize());
        return pageSql.toString();
    }

    /**
     * 获取总记录数
     *
     * @param sql
     * @param mappedStatement
     * @param boundSql
     */
    private long getTotal(String sql,
                          MappedStatement mappedStatement,
                          BoundSql boundSql) {
        // 记录总记录数
        String countSql = "SELECT COUNT(0) FROM (" + sql + ") tmp_total";
        DataSource dataSource = null;
        Connection connection = null;
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            dataSource = mappedStatement.getConfiguration().getEnvironment().getDataSource();
            connection = dataSource.getConnection();
            countStmt = connection.prepareStatement(countSql);
            BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(),
                    countSql,
                    boundSql.getParameterMappings(),
                    boundSql.getParameterObject());
            setParameters(countStmt, mappedStatement, countBoundSql, boundSql.getParameterObject());
            rs = countStmt.executeQuery();
            long total = 0;
            if (rs.next()) {
                total = rs.getInt(1);
            }
            return total;
        } catch (SQLException e) {
            logger.error("Ignore this exception", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("Ignore this exception", e);
            }
            try {
                countStmt.close();
            } catch (SQLException e) {
                logger.error("Ignore this exception", e);
            }
            // *******
            DataSourceUtils.releaseConnection(connection,dataSource);
        }
        return 0;
    }

    /**
     * 代入参数值
     *
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
                               Object parameterObject) throws SQLException {
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler.setParameters(ps);
    }
}
