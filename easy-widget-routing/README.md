
### 组件介绍
````
1、LazyConnectionDataSourceProxy  
   参考地址 https://blog.csdn.net/dalinsi/article/details/53788729

2、动态刷新数据源
   参考地址 https://github.com/apolloconfig/apollo/issues/1254
````

### saas 分库 动态路由
1、在spring.yaml 配置如下 （注意分库键、广播表，db1、db2配置项目与之一一对应）
````
routing:
  rules:
    shardingColumns:
      - medins_no
      - yljgdm
    broadcastTables:
      - broadcast,a,b,c,d
      - e,f,a
  databaseProperties:
    driverClassName: com.mysql.cj.jdbc.Driver
    minPoolSize: 1
    maxIdleTime: 1800
    maxPoolSize: 5
  databases:
    db1:
      url: jdbc:mysql://192.168.90.57:3306/test1?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2B8&rewriteBatchedStatements=true
      username: root
      password: hmap
    db2:
      url: jdbc:mysql://192.168.90.57:3306/test2?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2B8&rewriteBatchedStatements=true
      username: root
      password: hmap
````
2、支持强行指定数据源
````
@HintRouting("db1")
````