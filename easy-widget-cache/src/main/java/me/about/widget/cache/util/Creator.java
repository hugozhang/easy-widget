package me.about.widget.cache.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

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

    public static void main(String[] args) throws Exception {
        Method method = Creator.class.getMethod("doWork");
        Class<?> returnType = method.getReturnType();

        System.out.println("Return type: " + returnType);
        // 输出: Return type: void
        System.out.println(returnType == void.class);


        Cache<Object, Object> cache = Caffeine.newBuilder()
                // 最大容量为1
                .initialCapacity(1000)
                .maximumSize(100_000)
                .removalListener((key, value, cause) ->
                        System.out.println("key:" + key + ",value:" + value + ",删除原因:" + cause))
//                .expireAfterAccess(3, TimeUnit.SECONDS)
                .expireAfterWrite(10, TimeUnit.MINUTES)

                .build();
        cache.put("java金融", "java金融");
        Thread.sleep(1 * 1000);
        System.out.println(cache.getIfPresent("java金融"));
        Thread.sleep(1 * 1000);
        cache.invalidate("java金融");
        System.out.println(cache.getIfPresent("java金融"));
        Thread.sleep(1 * 1000);
        System.out.println(cache.getIfPresent("java金融"));
        Thread.sleep(3004);
        System.out.println(cache.getIfPresent("java金融"));
    }



    /**
     * * 缓存中找不到，则会进入这个方法。一般是从数据库获取内容
     * * @param k
     * * @return
     */
    private static String getValue(String k) {
        return k + ":value";
    }
    public void doWork() {
        // 这是一个void方法
    }
}
