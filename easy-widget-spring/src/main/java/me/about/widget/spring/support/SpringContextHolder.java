package me.about.widget.spring.support;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * spring context holder
 *
 * @author: hugo.zxh
 * @date: 2022/04/07 10:20
 * @description:
 */
@Slf4j
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    @Getter
    private static ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(String name,Class<T> requiredType) {
        return (T) applicationContext.getBean(name,requiredType);
    }
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static void clearHolder() {
        applicationContext = null;
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.clearHolder();
    }

    /**
     * 设置spring上下文
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }
}
