package me.about.widget.mybatis.proxy;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 动态加载XML文件解析
 *
 * @author: hugo.zxh
 * @date: 2022/02/10 15:22
 * @description:
 */
public class SqlHandler implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlHandler.class);

    private Configuration configuration;

    public SqlHandler(String resource) throws IOException {
        configuration = new Configuration();
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, configuration, resource,configuration.getSqlFragments());
            builder.parse();
        } finally {
            LOGGER.info("配置文件解析完成:{}",resource);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MappedStatement mappedStatement = configuration.getMappedStatement(method.getName());
        Object param = null;
        if (args != null && args.length > 0) {
            param = args[0];
        }
        BoundSql boundSql = mappedStatement.getBoundSql(param);
        // 处理动态参数#{param}
        String sql = dynamicSqlHandler(boundSql);
        return sql;
    }

    /**
     * 获取参数
     *
     * @param param Object类型参数
     * @return 转换之后的参数
     */
    private static String getParameterValue(Object param) {
        if (param == null) {
            return "null";
        }
        if (param instanceof Number) {
            return param.toString();
        }
        String value;
        if (param instanceof String) {
            value = "'" + param.toString() + "'";
        } else if (param instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(param) + "'";
        } else if (param instanceof Enum) {
            value = "'" + ((Enum<?>) param).name() + "'";
        } else {
            value = param.toString();
        }
        return value;
    }

    /**
     * 处理动态sql中的占位符?
     *
     * @param boundSql
     */
    private String dynamicSqlHandler(BoundSql boundSql) {
        String sql = boundSql.getSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (parameterMappings != null) {
            String parameter = "null";
            String propertyName;
            MetaObject newMetaObject = configuration.newMetaObject(parameterObject);
            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() == ParameterMode.OUT) {
                    continue;
                }
                propertyName = parameterMapping.getProperty();
                if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    parameter = getParameterValue(parameterObject);
                } else if (newMetaObject.hasGetter(propertyName)) {
                    parameter = getParameterValue(newMetaObject.getValue(propertyName));
                } else if (boundSql.hasAdditionalParameter(propertyName)) {
                    parameter = getParameterValue(boundSql.getAdditionalParameter(propertyName));
                }
                sql = sql.replaceFirst("\\?", parameter);
            }
        }
        return sql;
    }
}
