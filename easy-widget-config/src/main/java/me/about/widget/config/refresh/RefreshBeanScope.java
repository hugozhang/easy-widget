package me.about.widget.config.refresh;

import me.about.widget.config.refresh.constant.RefreshBeanConstant;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 刷新 bean
 *
 * @author: hugo.zxh
 * @date: 2023/11/07 23:20
 */
public class RefreshBeanScope implements Scope, BeanDefinitionRegistryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RefreshBeanScope.class);

    private final Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

    private Map<String, ReadWriteLock> locks = new ConcurrentHashMap<>();

    protected ReadWriteLock getLock(String beanName) {
        return this.locks.get(beanName);
    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if(cache.containsKey(name)){
            return cache.get(name);
        }
        Object bean = objectFactory.getObject();
        cache.put(name,bean);

        return bean;
    }

    @Override
    public Object remove(String name) {
        return cache.remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return "refreshBeanScope";
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (String name : registry.getBeanDefinitionNames()) {
            BeanDefinition definition = registry.getBeanDefinition(name);
            if (definition instanceof RootBeanDefinition) {
                RootBeanDefinition root = (RootBeanDefinition) definition;
                BeanDefinitionHolder decoratedDefinition = root.getDecoratedDefinition();
                if (decoratedDefinition != null
                        && root.hasBeanClass()
                        && root.getBeanClass() == ScopedProxyFactoryBean.class) {
                    if (RefreshBeanConstant.SCOPE_NAME.equals(decoratedDefinition.getBeanDefinition().getScope())) {
                        root.setBeanClass(RefreshBeanScope.LockedScopedProxyFactoryBean.class);
                        root.getConstructorArgumentValues().addGenericArgumentValue(this);
                        // surprising that a scoped proxy bean definition is not already
                        // marked as synthetic?
                        root.setSynthetic(true);
                    }
                }
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerScope(RefreshBeanConstant.SCOPE_NAME,this);
    }


    public static class LockedScopedProxyFactoryBean<S extends RefreshBeanScope>
            extends ScopedProxyFactoryBean implements MethodInterceptor {

        private final S scope;

        private String targetBeanName;

        public LockedScopedProxyFactoryBean(S scope) {
            this.scope = scope;
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
            super.setBeanFactory(beanFactory);
            Object proxy = getObject();
            if (proxy instanceof Advised) {
                Advised advised = (Advised) proxy;
                advised.addAdvice(0, this);
            }
        }

        @Override
        public void setTargetBeanName(String targetBeanName) {
            super.setTargetBeanName(targetBeanName);
            this.targetBeanName = targetBeanName;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            if (AopUtils.isEqualsMethod(method) || AopUtils.isToStringMethod(method)
                    || AopUtils.isHashCodeMethod(method)
                    || isScopedObjectGetTargetObject(method)) {
                return invocation.proceed();
            }
            Object proxy = getObject();
            ReadWriteLock readWriteLock = this.scope.getLock(this.targetBeanName);
            if (readWriteLock == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("For bean with name [" + this.targetBeanName
                            + "] there is no read write lock. Will create a new one to avoid NPE");
                }
                readWriteLock = new ReentrantReadWriteLock();
            }
            Lock lock = readWriteLock.readLock();
            lock.lock();
            try {
                if (proxy instanceof Advised) {
                    Advised advised = (Advised) proxy;
                    ReflectionUtils.makeAccessible(method);
                    return ReflectionUtils.invokeMethod(method,
                            advised.getTargetSource().getTarget(),
                            invocation.getArguments());
                }
                return invocation.proceed();
            }
            // see gh-349. Throw the original exception rather than the
            // UndeclaredThrowableException
            catch (UndeclaredThrowableException e) {
                throw e.getUndeclaredThrowable();
            }
            finally {
                lock.unlock();
            }
        }

        private boolean isScopedObjectGetTargetObject(Method method) {
            return method.getDeclaringClass().equals(ScopedObject.class)
                    && method.getName().equals("getTargetObject")
                    && method.getParameterTypes().length == 0;
        }

    }

}
