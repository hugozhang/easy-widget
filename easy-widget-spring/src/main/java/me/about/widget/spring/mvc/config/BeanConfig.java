package me.about.widget.spring.mvc.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.ResourceServlet;
import com.alibaba.druid.support.http.StatViewFilter;
import me.about.widget.spring.mvc.security.ConcurrentSessionUserFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class BeanConfig {

    @Value("${me.about.widget.spring.match-url:/hmap}")
    private String matchUrl;

    @Value("${druid.stat-view.login-username:winning}")
    private String username;

    @Value("${druid.stat-view.login-password:Winning@123}")
    private String password;

    @Bean
    public FilterRegistrationBean<ConcurrentSessionUserFilter> registerAuthFilter() {
        FilterRegistrationBean<ConcurrentSessionUserFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ConcurrentSessionUserFilter());
        registration.addUrlPatterns(matchUrl + "/*");
        registration.setName("ConcurrentSessionUserFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }

    @Bean
    @ConditionalOnClass(value = {DruidDataSource.class,StatViewFilter.class})
    public FilterRegistrationBean<StatViewFilter> registerDruidStatViewFilter() {
        FilterRegistrationBean<StatViewFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new StatViewFilter());
        registration.addInitParameter(ResourceServlet.PARAM_NAME_USERNAME, username);
        registration.addInitParameter(ResourceServlet.PARAM_NAME_PASSWORD, password);
        registration.addUrlPatterns("/druid/*");
        registration.setName("DruidStatViewFilter");
        registration.setOrder(2);
        return registration;
    }

}
