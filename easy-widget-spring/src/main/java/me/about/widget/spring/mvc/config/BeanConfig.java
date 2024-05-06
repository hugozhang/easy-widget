package me.about.widget.spring.mvc.config;


import me.about.widget.spring.mvc.security.ConcurrentSessionUserFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class BeanConfig {

    @Value("${me.about.widget.spring.match-url:/hmap}")
    private String matchUrl;

    @Bean
    public ConcurrentSessionUserFilter concurrentSessionFilter() {
        return new ConcurrentSessionUserFilter();
    }

    @Bean
    public FilterRegistrationBean<ConcurrentSessionUserFilter> registerAuthFilter() {
        FilterRegistrationBean<ConcurrentSessionUserFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(concurrentSessionFilter());
        registration.addUrlPatterns(matchUrl + "/*");
        registration.setName("concurrentSessionFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }

}
