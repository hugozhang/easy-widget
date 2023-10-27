package me.about.widget.config.spring.property;

import lombok.Getter;
import org.springframework.core.MethodParameter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * SpringValue
 *
 * @author: hugo.zxh
 * @date: 2023/02/03 22:25
 * @description:
 */
@Getter
public class SpringValue {

    private MethodParameter methodParameter;

    /**
     * bean 的弱引用
     */
    private WeakReference<Object> beanRef;
    /**
     * bean 名称
     */
    private String beanName;
    /**
     * 字段
     */
    private Field field;
    /**
     * 属性的键
     */
    private String key;
    /**
     * 对应的占位符
     */
    private String placeholder;
    /**
     * 字段的类对象
     */
    private Class<?> targetType;

    /**
     *
     * @param key
     * @param placeholder
     * @param bean
     * @param beanName
     * @param field
     */
    public SpringValue(String key, String placeholder, Object bean, String beanName, Field field) {
        this.beanRef = new WeakReference<>(bean);
        this.beanName = beanName;
        this.field = field;
        this.key = key;
        this.placeholder = placeholder;
        this.targetType = field.getType();
    }

    /**
     *
     * @param key
     * @param placeholder
     * @param bean
     * @param beanName
     * @param method
     */
    public SpringValue(String key, String placeholder, Object bean, String beanName, Method method) {
        this.beanRef = new WeakReference<>(bean);
        this.beanName = beanName;
        this.methodParameter = new MethodParameter(method, 0);
        this.key = key;
        this.placeholder = placeholder;
        Class<?>[] paramTps = method.getParameterTypes();
        this.targetType = paramTps[0];
    }


    public void update(Object newVal) throws IllegalAccessException, InvocationTargetException {
        if (isField()) {
            injectField(newVal);
        } else {
            injectMethod(newVal);
        }
    }


    /**
     * 使用反射，给字段注入新的值
     *
     * @param newVal 新的值
     * @throws IllegalAccessException 发送反射异常时
     */
    private void injectField(Object newVal) throws IllegalAccessException {
        Object bean = beanRef.get();
        if (bean == null) {
            return;
        }
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(bean, newVal);
        field.setAccessible(accessible);
    }

    private void injectMethod(Object newVal)
            throws InvocationTargetException, IllegalAccessException {
        Object bean = beanRef.get();
        if (bean == null) {
            return;
        }
        methodParameter.getMethod().invoke(bean, newVal);
    }

    public boolean isField() {
        return this.field != null;
    }

    @Override
    public String toString() {
        Object bean = beanRef.get();
        if (bean == null) {
            return "";
        }
        if (isField()) {
            return String
                    .format("key: %s, beanName: %s, field: %s.%s", key, beanName, bean.getClass().getName(), field.getName());
        }
        return String.format("key: %s, beanName: %s, method: %s.%s", key, beanName, bean.getClass().getName(),
                methodParameter.getMethod().getName());
    }

}

