
### Mock User Filter

````
package com.winning.hmap.container.config;

import com.winning.hmap.container.spring.MockUserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {


    @Bean
    public MockUserFilter mockFilter() {
        return new MockUserFilter();
    }

    @Bean
    public FilterRegistrationBean<MockUserFilter> registerAuthFilter() {
        FilterRegistrationBean<MockUserFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(mockFilter());
        registration.addUrlPatterns("/*");
        registration.setName("mockUserFilter");
        registration.setOrder(1);
        return registration;
    }

}

````

### 自定义验证器

```
@PositiveNumberList
@ApiModelProperty("科室id数组")
private List<Long> deptIds;

@ApiModelProperty(value = "性别")
@EnumString(value = {"F","M"}, message="性别只允许为F或M")
private String sex;
```

### 踢用户
同一个用户在多个地方登录，踢掉之前登录的
```
org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy
org.springframework.session.security.SpringSessionBackedSessionRegistry
org.springframework.session.data.redis.RedisIndexedSessionRepository
```

### druid 监控
```
 <bean id="wallConfig" class="com.alibaba.druid.wall.WallConfig">
        <constructor-arg value="META-INF/druid/wall/mysql"/>
        <property name="multiStatementAllow" value="true"/>
        <property name="selectUnionCheck" value="false"/>
        <property name="strictSyntaxCheck" value="false"/>
    </bean>

    <bean id="wallFilter" class="com.alibaba.druid.wall.WallFilter">
        <property name="dbType" value="mysql"/>
        <property name="config" ref="wallConfig"/>
    </bean>

    <bean id="logFilter" class="com.alibaba.druid.filter.logging.Log4jFilter">
        <property name="statementExecutableSqlLogEnable" value="true"/>
    </bean>
    <bean id="statfilter" class="com.alibaba.druid.filter.stat.StatFilter">
        <property name="mergeSql" value="true"/>
        <property name="slowSqlMillis" value="1000"/>
        <property name="logSlowSql" value="true"/>
    </bean>


    <bean id="druid-stat-interceptor"
          class="com.alibaba.druid.support.spring.stat.DruidStatInterceptor">
    </bean>

    <bean id="druid-stat-pointcut"
          class="org.springframework.aop.support.JdkRegexpMethodPointcut"
          scope="prototype">
        <property name="patterns">
            <list>
                <value>com.winning.hmap.*.*.service.*</value>
            </list>
        </property>
    </bean>

    <aop:config>
        <aop:advisor advice-ref="druid-stat-interceptor" pointcut-ref="druid-stat-pointcut" />
    </aop:config>
```