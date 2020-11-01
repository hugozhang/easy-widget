package me.about.widget.excel;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/01 23:15
 * @Description:
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
