package me.about.widget.excel;

/**
 * 实例化
 *
 * @author: hugo.zxh
 * @date: 2020/11/01 23:15
 * @description:
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
