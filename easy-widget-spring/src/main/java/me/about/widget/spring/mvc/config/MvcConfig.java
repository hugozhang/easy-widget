package me.about.widget.spring.mvc.config;

import me.about.widget.spring.mvc.security.SessionInterceptor;
import me.about.widget.spring.mvc.security.SessionUserArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * mvc 配置
 *
 * @author: hugo.zxh
 * @date: 2023/11/11 17:22
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("#{'${session.path.patterns:/v2/**}'.split(',')}")
    private String[] pathPatterns;

    @Value("#{'${session.exclude.path.patterns:/v2/**/login,/v2/**/logout,/v2/license/**}'.split(',')}")
    private String[] excludePathPatterns;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //当前系统已经默认使用jackson，内部bean已经有相关注解  @RequestBody String json 有问题
//        converters.add(0, new MappingJackson2HttpMessageConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SessionUserArgumentResolver());
    }

    @Bean
    public SessionInterceptor getSessionInterceptor(){
        return new SessionInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getSessionInterceptor())
                .addPathPatterns(pathPatterns) //拦截所有接口
                .excludePathPatterns(excludePathPatterns); //排除这些接口
    }

}
