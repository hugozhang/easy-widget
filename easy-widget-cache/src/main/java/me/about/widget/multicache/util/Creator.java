package me.about.widget.multicache.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if (tClass == Map.class) {
                return (T) new HashMap<>(16);
            }
            if (tClass == List.class) {
                return (T) new ArrayList<>();
            }
            return tClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate object of type " + tClass.getCanonicalName(), e);
        }
    }
}
