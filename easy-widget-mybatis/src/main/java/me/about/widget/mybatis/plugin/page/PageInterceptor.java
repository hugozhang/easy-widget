package me.about.widget.mybatis.plugin.page;

import com.google.common.collect.Lists;
import me.about.widget.mybatis.plugin.page.model.PageParam;
import me.about.widget.mybatis.plugin.page.model.PageResult;
import me.about.widget.mybatis.plugin.page.spring.InternalResult;
import me.about.widget.mybatis.plugin.page.spring.InternalResultContext;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;
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

    private static final String countSuffix = "_COUNT";

    private static final CountSqlParser countSqlParser = new CountSqlParser();

    private Method getMethod(MappedStatement mappedStatement) throws ClassNotFoundException {
        String id = mappedStatement.getId();
        String className = id.substring(0, id.lastIndexOf("."));
        String methodName = id.substring(id.lastIndexOf(".") + 1);

        final Method[] methods = Class.forName(className).getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) &&
                    PageResult.class.isAssignableFrom(method.getReturnType())) {
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


        Method method = getMethod(ms);

        PageParam<?> pageParam = findPageParameter(parameter);

        if (method == null || pageParam == null) {
            return invocation.proceed();
        }

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

        int pageSize = pageParam.getPageSize() <= 0 ? 10 : pageParam.getPageSize();
        int currentPage = pageParam.getCurrentPage() <= 0 ? 1 : pageParam.getCurrentPage();
        pageParam.setCurrentPage(currentPage);

        String pageSql = buildPageSql(boundSql, pageParam);

        // 用方法返回类型里的泛型参数构建新的ResultMap
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
        Class<?> resolve = resolvableType.getGeneric(0).resolve();

        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration()
                , ms.getId(), resolve, Lists.newArrayList())
                .build();

        MappedStatement internalMs = new MappedStatement.Builder(ms.getConfiguration()
                , ms.getId(), ms.getSqlSource(), ms.getSqlCommandType()).resultMaps(Lists.newArrayList(resultMap)).build();

        // 分页 BoundSql
        BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql
                , boundSql.getParameterMappings(), boundSql.getParameterObject());

        Map<String, Object> additionalParameters = ExecutorHelper.getAdditionalParameter(boundSql);
        for (String key : additionalParameters.keySet()) {
            pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }

        Object proceed;

        // 重设分页参数里的总页数等
        long total = getTotal(executor,ms, boundSql);
        int totalPage = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));

        if (total == 0 || pageParam.getCurrentPage() > totalPage) {
            proceed = Lists.newArrayList();
        } else {
            proceed = executor.query(internalMs,parameter,rowBounds,resultHandler,cacheKey,pageBoundSql);
        }

        // 设置返回值
        InternalResult<?> internalResult = new InternalResult<>();
        internalResult.setTotal(total);
        internalResult.setTotalPage(totalPage);
        internalResult.setRows((List)proceed);
        // ms.getId()  类名+ "." + 方法名
        InternalResultContext.setResult(ms.getId(),internalResult);

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


    public String buildPageSql(BoundSql boundSql, PageParam<?> pageParam) {
        return boundSql.getSql() + " LIMIT " + (pageParam.getCurrentPage() - 1) * pageParam.getPageSize() + "," + pageParam.getPageSize();
    }

    /**
     * 获取总记录数
     *
     * @param ms
     * @param boundSql
     */
    private long getTotal(Executor executor,MappedStatement ms,
                          BoundSql boundSql) throws SQLException {
        String countMsId = ms.getId() + countSuffix;
        // 记录总记录数

        String countSql = countSqlParser.getSmartCountSql(boundSql.getSql());

//        String countSql = "SELECT COUNT(0) FROM (" + boundSql.getSql() + ") auto_gen_total";
        logger.debug("countSql:{}", countSql);

        BoundSql countBoundSql = new BoundSql(ms.getConfiguration(), countSql
                , boundSql.getParameterMappings()
                , boundSql.getParameterObject());
        Map<String, Object> additionalParameters = ExecutorHelper.getAdditionalParameter(boundSql);
        //当使用动态 SQL 时，可能会产生临时的参数，这些参数需要手动设置到新的 BoundSql 中
        for (String key : additionalParameters.keySet()) {
            countBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }
        MappedStatement countMs = ExecutorHelper.newCountMappedStatement(ms, countMsId);

        CacheKey countKey = executor.createCacheKey(countMs, countBoundSql.getParameterObject(), RowBounds.DEFAULT, boundSql);

        Object countResultList = executor.query(countMs, boundSql.getParameterObject(), RowBounds.DEFAULT, null, countKey, countBoundSql);
        Long count = (Long) ((List) countResultList).get(0);
        return count;
    }
}
