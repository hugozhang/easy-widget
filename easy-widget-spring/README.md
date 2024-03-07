
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