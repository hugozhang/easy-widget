package me.about.widget.spring.mvc.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
/**
 * 访问地址 <a href="http://ip:port/swagger-ui/index.html">...</a>
 */

//@Configuration
@Slf4j
//@EnableOpenApi
public class SwaggerConfig {

    @Value("${swagger.enable:true}")
    private Boolean swaggerEnable;


    @Bean
    @Scope("prototype") // 设置为原型作用域，每次请求都会创建新的实例
    public Docket createDocket(ModuleInfo moduleInfo) {
        return new Docket(DocumentationType.OAS_30)
                .groupName(moduleInfo.getModuleName())
                .apiInfo(apiInfo(moduleInfo.getModuleName()))
                .enable(swaggerEnable)
                .select()
                .apis(RequestHandlerSelectors.basePackage(moduleInfo.getModuleBasePackage()))
                .paths(PathSelectors.any())
                .build();
    }


    @Resource
    private DefaultListableBeanFactory beanFactory;

    @PostConstruct
    public void initSwaggerForModules() {
        Arrays.stream(ModuleInfo.values()).forEach(module -> {
            beanFactory.registerSingleton(
                    "docket_" + module.getModuleName(),
                    beanFactory.getBean(SwaggerConfig.class).createDocket(module));
        });
    }


    private ApiInfo apiInfo(String title) {
        return new ApiInfoBuilder()
                .title(title)
                .contact(new Contact("院端架构", "", "admin@winning.com"))
                .version("1.0.0")
                .build();
    }

}
