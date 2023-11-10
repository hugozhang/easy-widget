package me.about.widget.config.refresh;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;

/**
 * scope 配置
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 23:16
 */
public class RefreshBeanScopeAutoConfiguration {

//    @Bean
//    public CustomScopeConfigurer customScopeConfigurer() {
//        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
//        Map<String, Object> scopes = new HashMap<>();
//        scopes.put(RefreshBeanConstant.SCOPE_NAME, new RefreshBeanScope());
//        configurer.setScopes(scopes);
//        return configurer;
//    }

//    @Bean
//    public static RefreshBeanScopeDefinitionRegistryPostProcessor refreshBeanScopeDefinitionRegistryPostProcessor() {
//        return new RefreshBeanScopeDefinitionRegistryPostProcessor();
//    }

    @Bean
    public RefreshBeanScope RefreshBeanScope() {
        return new RefreshBeanScope();
    }

    @Bean
    public RefreshBeanScopeContext refreshBeanScopeContext(DefaultListableBeanFactory beanFactory) {
        return new RefreshBeanScopeContext(beanFactory);
    }

}
