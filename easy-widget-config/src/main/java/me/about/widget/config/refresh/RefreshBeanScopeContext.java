package me.about.widget.config.refresh;

import me.about.widget.config.refresh.constant.RefreshBeanConstant;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RefreshBeanScopeContext  scope 上下文
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 23:28
 */
public class RefreshBeanScopeContext implements ApplicationListener<RefreshBeanEvent> {

    private final DefaultListableBeanFactory beanFactory;

    public RefreshBeanScopeContext(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public List<String> refreshBean() {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        List<String> refreshBeanDefinitionNames = new ArrayList<>();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            if(RefreshBeanConstant.SCOPE_NAME.equals(beanDefinition.getScope())) {
                beanFactory.destroyScopedBean(beanDefinitionName);
//                beanFactory.getBean(beanDefinitionName);
                refreshBeanDefinitionNames.add(beanDefinitionName);
            }
        }
        return Collections.unmodifiableList(refreshBeanDefinitionNames);
    }

    @Override
    public void onApplicationEvent(RefreshBeanEvent event) {
//        Set<String> keys = event.getKeys();
//        for (String key : keys) {
//            if (RefreshBeanScope.keySpringValueMap.containsKey(key)) {
//                SpringValue springValue = RefreshBeanScope.keySpringValueMap.get(key);
//                beanFactory.destroyScopedBean(springValue.getBeanName());
//            }
//        }
        refreshBean();
    }
}