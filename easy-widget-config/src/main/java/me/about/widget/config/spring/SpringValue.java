package me.about.widget.config.spring;

import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * SpringValue
 *
 * @author: hugo.zxh
 * @date: 2023/02/03 22:25
 * @description:
 */
@Getter
public class SpringValue {

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

    @SneakyThrows
    public void update(Object newVal) {
        injectField(newVal);
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

    @Override
    public String toString() {
        Object bean = beanRef.get();
        if (bean == null) {
            return "";
        }
        return String.format("key: %s, beanName: %s, field: %s.%s", key, beanName, bean.getClass().getName(), field.getName());
    }

}

