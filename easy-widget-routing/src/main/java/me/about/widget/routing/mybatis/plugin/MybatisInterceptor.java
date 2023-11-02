package me.about.widget.routing.mybatis.plugin;

import com.google.common.collect.Lists;
import me.about.widget.routing.RoutingContext;
import me.about.widget.routing.spring.DbSelect;
import me.about.widget.routing.sqlparse.DruidSqlParse;
import me.about.widget.routing.sqlparse.model.ConditionField;
import me.about.widget.routing.sqlparse.model.SqlParseResult;
import me.about.widget.routing.sqlparse.model.TableOperator;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})

public class MybatisInterceptor implements Interceptor {

    private final static Logger logger = LoggerFactory.getLogger(RoutingInterceptor.class);


    private DbSelect getMapperMethodAnnotation(MappedStatement mappedStatement) throws ClassNotFoundException {
        String id = mappedStatement.getId();
        String className = id.substring(0, id.lastIndexOf("."));
        String methodName = id.substring(id.lastIndexOf(".") + 1);
        final Method[] methods = Class.forName(className).getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method.getAnnotation(DbSelect.class);
            }
        }
        return null;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String sql = null;
        try {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

            Object parameter = null;
            if (invocation.getArgs().length > 1) {
                parameter = invocation.getArgs()[1];
            }
            String sqlId = mappedStatement.getId();
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            
            sql = showSql(configuration, boundSql);

            DbSelect mapperMethodAnnotation = getMapperMethodAnnotation(mappedStatement);
            if (mapperMethodAnnotation != null) {
                String value = mapperMethodAnnotation.value();
                RoutingContext.setRoutingDatabase(value);
                return invoke(invocation,sqlId,sql);
            }

            SqlParseResult parseResult = DruidSqlParse.parse(sql);
            logger.debug(parseResult.toString());

            Set<TableOperator> tableOperatorSet = parseResult.getTableOperators();
            Set<ConditionField> conditionFieldSet = parseResult.getConditionFields();

            int len = conditionFieldSet.size();

            //同一个分区主键，值要一样
            if (len > 1) {
//                throw new BizException(400,"分区主键非法");
                logger.error("分区主键非法");
            }

            // 无分区键
            if (len == 0 ) {
                List<TableOperator> broadcasts = tableOperatorSet.stream()
                        .filter(tableOperator -> RoutingContext.getBroadcastTables().contains(tableOperator.getTableName()))
                        .collect(Collectors.toList());
                // 广播表不为空
                if (broadcasts.size() != 0) {
                    //确认读写操作
                    if ("Select".equalsIgnoreCase(broadcasts.get(0).getOperate())) {
                        RoutingContext.setRoutingDatabase(RoutingContext.getDatabaseIds().get(0));
                    } else {
                        List<CompletableFuture<Void>> futures = Lists.newArrayList();
                        List<Object> results = Lists.newArrayList();
                        for (String databaseId : RoutingContext.getDatabaseIds()) {
                            String finalSql1 = sql;
                            CompletableFuture<Void> future =  CompletableFuture.runAsync(() -> {
                                try {
                                    RoutingContext.setRoutingDatabase(databaseId);
                                    Object rst = invoke(invocation,sqlId, finalSql1);
                                    results.add(rst);
                                } catch (InvocationTargetException e) {
                                    logger.error(e.getMessage(),e);
                                } catch (IllegalAccessException e) {
                                    logger.error(e.getMessage(),e);
                                }
                            });
                            futures.add(future);
                        }
                        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                        allFutures.join();

                        return results == null || results.isEmpty() ? null : results.get(0);
                    }

                } else {
                    //无分区键  又不是广播表
//                    throw new BizException(400,"无分区键,又不是广播表");
                    logger.error("无分区键  又不是广播表");
                }
            }
            //单一分区
            if (len == 1) {
                ArrayList<ConditionField> conditionFields = Lists.newArrayList(conditionFieldSet);
                ConditionField conditionField = conditionFields.get(0);
                RoutingContext.setRoutingDatabase(conditionField.getValue());
            }
            return invoke(invocation,sqlId,sql);
        } catch (Throwable e) {
            logger.error("【SQL异常】" + sql,e);
            throw e;
        } finally {
            RoutingContext.clear();
        }
    }

    private Object invoke(Invocation invocation,String sqlId,String sql) throws InvocationTargetException, IllegalAccessException {
        long start = System.currentTimeMillis();
        Object returnValue = invocation.proceed();
        long end = System.currentTimeMillis();
        long time = (end - start);
        logger.info(getSql(sql, sqlId, time));
        return returnValue;
    }

    public static String getSql(String sql, String sqlId, long time) {
        StringBuilder str = new StringBuilder(100);
        str.append(sqlId);
        str.append("  执行SQL:【");
        str.append(sql);
        str.append("】   执行时间");
        str.append(":");
        str.append(time);
        str.append("ms");
        return str.toString();
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            if (obj != null) {
                value = "'" + formatter.format(((Date) obj)) + "'";
            }
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (!CollectionUtils.isEmpty(parameterMappings) && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties arg0) {
    }
}
