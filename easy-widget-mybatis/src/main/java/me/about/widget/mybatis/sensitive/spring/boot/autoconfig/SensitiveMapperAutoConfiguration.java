package me.about.widget.mybatis.sensitive.spring.boot.autoconfig;

import me.about.widget.mybatis.sensitive.spring.SensitiveMapperBeanPostProcessor;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 注册 SensitiveMapperAutoConfiguration
 *
 * @author: hugo.zxh
 * @date: 2023/03/22 15:28
 */
@Configuration
@ConditionalOnClass(MapperFactoryBean.class)
@AutoConfigureBefore(name="org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SensitiveMapperAutoConfiguration {

    private final static Logger LOGGER = LoggerFactory.getLogger(SensitiveMapperAutoConfiguration.class);
    
    @Bean
    public SensitiveMapperBeanPostProcessor sensitiveMapperBeanPostProcessor(){
        LOGGER.info("SensitiveMapperBeanPostProcessor create");
        return new SensitiveMapperBeanPostProcessor();
    }
}

