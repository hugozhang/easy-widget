
组件介绍
````
1. 多数据源切换支持

2. 多数据源事务失效解决

3. 参考实现
   https://blog.csdn.net/reee112/article/details/90442542
   https://www.wcqblog.com/article/detail/308648788896120832
   https://www.byun.com/thread-68410-1-1.html

4. 自定义解析xml

5. 原理参考
   https://www.ktanx.com/blog/p/2501

6. 敏感数据
   https://blog.csdn.net/why_still_confused/article/details/113060605

````

例子：
````
<!-- 注解式事务 -->
    <bean id="abstractDataSource" abstract="true" class="com.alibaba.druid.pool.DruidDataSource" destroy-method="close" init-method="init">
        <property name="maxWait" value="10000"/>
        <property name="poolPreparedStatements" value="true"/>
        <property name="maxPoolPreparedStatementPerConnectionSize" value="50"/>
        <property name="proxyFilters">
            <list>
                <ref bean="logFilter"/>
                <ref bean="statfilter"/>
                <ref bean="wallFilter"/>
            </list>
        </property>
        <property name="validationQuery" value="SELECT 1 "/>
        <property name="testOnBorrow" value="false"/>
        <property name="testOnReturn" value="false"/>
        <property name="testWhileIdle" value="true"/>
        <property name="asyncInit" value="true"/>
        <property name="connectionErrorRetryAttempts" value="0"/>
        <property name="breakAfterAcquireFailure" value="true"/>
    </bean>

    <bean id="dataSource" parent="abstractDataSource">
        <property name="driverClassName" value="${ds.driverClassName}"/>
        <property name="url" value="${ds.url}"/>
        <property name="username" value="${ds.username}"/>
        <property name="password" value="${ds.password}"/>
        <property name="initialSize" value="${ds.initialPoolSize}"/>
        <property name="minIdle" value="${ds.minPoolSize}"/>
        <property name="maxActive" value="${ds.maxPoolSize}"/>
    </bean>

    <bean id="rccDataSource" parent="abstractDataSource">
        <property name="driverClassName" value="${ds.middle.driverClassName}"/>
        <property name="url" value="${ds.middle.url}"/>
        <property name="username" value="${ds.middle.username}"/>
        <property name="password" value="${ds.middle.password}"/>
        <property name="initialSize" value="${ds.middle.initialPoolSize}"/>
        <property name="minIdle" value="${ds.middle.minPoolSize}"/>
        <property name="maxActive" value="${ds.middle.maxPoolSize}"/>
    </bean>

    <bean id="dynamicDataSource" class="me.about.widget.mybatis.function.multidatasource.DynamicDataSource">
        <property name="defaultTargetDataSource" ref="dataSource"></property>
        <property name="targetDataSources">
            <map>
                <entry key="db1" value-ref="dataSource"></entry>
                <entry key="db2" value-ref="rccDataSource"></entry>
            </map>
        </property>
    </bean>

    <bean class="me.about.widget.mybatis.function.multidatasource.DynamicDataSourceAspect"/>

    <bean id="transactionFactory" class="me.about.widget.mybatis.function.multidatasource.MultiDataSourceTransactionFactory"/>

    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="transactionFactory" ref="transactionFactory"/>
        <property name="dataSource" ref="dynamicDataSource"/>
        <property name="configLocation" value="classpath:mybatis-configuration.xml"/>
        <property name="mapperLocations">
            <array>
                <value>classpath*:mapper/*Mapper.xml</value>
                <value>classpath*:mapper/**/*Mapper.xml</value>
            </array>
        </property>
    </bean>

    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dynamicDataSource"></property>
    </bean>

    <tx:annotation-driven />
````