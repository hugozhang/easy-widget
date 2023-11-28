# Feature

结构介绍
````
1. @FormUrlEncoded表示Post方法提交的是键值对数据，对应content-type=application/x-www-form-urlencoded。参数注解为@Field、@FieldMap

2. @Multipart表示Post方法对应Content-Type: multipart/form-data，提交表单数据。参数注解为@Part，@PartMap

3. 对应content-type=application/json，提交数据到body。参数注解为@Body

````

例子：
````
@FormUrlEncoded
@POST("user/edit")
//@Field逐一设置
Call<User> updateUser(@Field("first_name") String first, @Field("last_name") String last);
//@FieldMap统一设置
@FormUrlEncoded
@POST("user/edit")
Call<User> updateUser(@FieldMap Map<String,String> fieldMap);

@Headers("X-Header: MessageBox")
@FormUrlEncoded
@POST("/rest/hmap/auth/login")
LoginResponse login(@Field("yh_mc") String username, @Field("yh_mm") String password, @Field("zhid") String tenantCode);

@POST("/rest/hmap/auth/logout")
LogoutResponse loginOut();

@GET("/hmap/portal/getConfigInfo")
R<String> getConfigInfo();


url带参数与body 一起使用
@POST("/cgi-bin/webhook/send")
String sendToBot(@Query ("key")String key,@Body WeChatMarkdown weChatMarkdown);


````

参考：
````
https://www.jianshu.com/p/0079156b7b98
https://github.com/square/retrofit
````

TODO
1、不同域名使用不同的池子管理