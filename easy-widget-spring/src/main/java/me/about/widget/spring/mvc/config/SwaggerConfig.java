package me.about.widget.spring.mvc.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.PostConstruct;
import java.util.Arrays;
/**
 * 访问地址 http://ip:端口/swagger-ui/index.html
 */

@Configuration
@Slf4j
//@EnableOpenApi
public class SwaggerConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${swagger.enable:true}")
    private Boolean swaggerEnable;


//    @Bean
    public Docket portal() {
        return new Docket(DocumentationType.OAS_30)
//                .ignoredParameterTypes(LoginUser.class)
                .groupName("portal模块")
                .apiInfo(apiInfo(""))
                .enable(swaggerEnable)
                .select()
//                .apis(RequestHandlerSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com.winning.hmap.portal"))
//                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)
//                        .or(RequestHandlerSelectors.withClassAnnotation(Controller.class)))
                .paths(PathSelectors.any())
                .build();
    }

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


    @Autowired
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

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) { // 只处理顶级 Spring 应用上下文
//            registerDocket(event.getApplicationContext());
        }
    }


    private void registerDocket(ApplicationContext applicationContext) {
        Docket docket = registerBean(Docket.class, applicationContext);
        docket
                .groupName("portal模块")
                .apiInfo(apiInfo(""))
                .enable(swaggerEnable)
                .select()
//                .apis(RequestHandlerSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com.winning.hmap.portal"))
//                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)
//                        .or(RequestHandlerSelectors.withClassAnnotation(Controller.class)))
                .paths(PathSelectors.any())
                .build();
//        registerBean(docket,applicationContext);
//        return docket;
    }

    public static void registerBean(Object bean, ApplicationContext applicationContext) {
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(bean.getClass());
        AbstractBeanDefinition definition = builder.getBeanDefinition();
        String name = BeanDefinitionReaderUtils.registerWithGeneratedName(definition, factory);
        factory.registerSingleton(name, bean);
        factory.autowireBean(bean);
    }

    public <T> T registerBean(Class<T> beanClass,ApplicationContext applicationContext) {
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);


        builder.addConstructorArgValue(DocumentationType.OAS_30);

//        builder.addPropertyReference("groupName", "portal模块");
        AbstractBeanDefinition definition = builder.getBeanDefinition();


        String name = BeanDefinitionReaderUtils.registerWithGeneratedName(definition, factory);

//        System.out.println("注册bean：" + definition.getBeanClassName());
//
//        String beanName = beanClass.getSimpleName();
//        factory.registerBeanDefinition(beanName, definition);
        factory.autowireBean(applicationContext.getBean(name,beanClass));
        return applicationContext.getBean(name,beanClass);
    }


//        beanDefinitionBuilder -> {
//            // 放入构造参数
//            if (!CollectionUtils.isEmpty(args)) {
//                args.forEach(beanDefinitionBuilder::addConstructorArgValue);
//            }
//            // 放入属性
//            if (property != null && !property.isEmpty()) {
//                property.forEach(beanDefinitionBuilder::addPropertyValue);
//            }
//            return beanDefinitionBuilder.getBeanDefinition();
//
//        }

}
