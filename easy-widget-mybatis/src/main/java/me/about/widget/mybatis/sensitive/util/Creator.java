package me.about.widget.mybatis.sensitive.util;

/**
 * 实例化
 *
 * @author: hugo.zxh
 * @date: 2023/03/24 17:16
 */
public class Creator {

    public static <T> T of(Class<T> class1) {
        try {
            return class1.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate object of type " + class1.getCanonicalName(), e);
        }
    }

}
