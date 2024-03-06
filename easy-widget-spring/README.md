
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