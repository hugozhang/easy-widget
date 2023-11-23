package me.about.widget.spring.mvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
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

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //当前系统已经默认使用jackson，内部bean已经有相关注解  @RequestBody String json 有问题
//        converters.add(0, new MappingJackson2HttpMessageConverter());
    }

}
