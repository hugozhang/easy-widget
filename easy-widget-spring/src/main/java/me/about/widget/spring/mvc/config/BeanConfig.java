package me.about.widget.spring.mvc.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.ResourceServlet;
import com.alibaba.druid.support.http.StatViewFilter;
import com.alibaba.druid.support.http.WebStatFilter;
import me.about.widget.spring.mvc.security.ConcurrentSessionUserFilter;
import me.about.widget.spring.mvc.security.SessionUserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Value("${me.about.widget.spring.match-url:/hmap}")
    private String matchUrl;

    @Value("${druid.stat-view.login-username:winning}")
    private String username;

    @Value("${druid.stat-view.login-password:Winning@123}")
    private String password;

    @Bean
    public ConcurrentSessionUserFilter concurrentSessionUserFilter() {
        return new ConcurrentSessionUserFilter();
    }

    @Bean
    public FilterRegistrationBean<ConcurrentSessionUserFilter> registerConcurrentSessionUserFilter() {
        FilterRegistrationBean<ConcurrentSessionUserFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(concurrentSessionUserFilter());
        registration.addUrlPatterns(matchUrl + "/*");
        registration.setName("ConcurrentSessionUserFilter");
        return registration;
    }

    @Bean
    @ConditionalOnClass(value = {DruidDataSource.class,StatViewFilter.class})
    public FilterRegistrationBean<StatViewFilter> statViewFilter() {
        FilterRegistrationBean<StatViewFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new StatViewFilter());
        registration.addInitParameter(ResourceServlet.PARAM_NAME_USERNAME, username);
        registration.addInitParameter(ResourceServlet.PARAM_NAME_PASSWORD, password);
        registration.addUrlPatterns("/druid/*");
        registration.setName("DruidStatViewFilter");
        return registration;
    }

    @Bean
    @ConditionalOnClass(value = {DruidDataSource.class,WebStatFilter.class})
    public FilterRegistrationBean<WebStatFilter> webStatFilter(){
        FilterRegistrationBean<WebStatFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new WebStatFilter());
        registration.addInitParameter(WebStatFilter.PARAM_NAME_PRINCIPAL_SESSION_NAME, SessionUserContext.SESSION_USER);
        registration.addInitParameter(WebStatFilter.PARAM_NAME_PROFILE_ENABLE, "true");
        registration.addUrlPatterns(matchUrl + "/*");
        registration.setName("WebStatFilter");
        return  registration;
    }

}
