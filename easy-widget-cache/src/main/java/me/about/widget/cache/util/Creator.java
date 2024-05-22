package me.about.widget.cache.util;

/**
 * 根据类创建实例
 *
 * @author: hugo.zxh
 * @date: 2020/11/01 23:15
 * @description:
 */
public class Creator {
    public static <T> T of(Class<T> tClass) {
        try {
            return tClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate object of type " + tClass.getCanonicalName(), e);
        }
    }
}
