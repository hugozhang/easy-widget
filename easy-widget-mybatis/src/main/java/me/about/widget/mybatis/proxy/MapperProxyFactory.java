package me.about.widget.mybatis.proxy;

import java.io.IOException;
import java.lang.reflect.Proxy;

/**
 * Mapper 代理
 *
 * @author: hugo.zxh
 * @date: 2022/02/10 15:24
 * @description:
 */
public class MapperProxyFactory {
    public static Object getMapper(Class<?> mapperClass, String resource ) throws IOException {
        return  Proxy.newProxyInstance(mapperClass.getClassLoader(),new Class[]{mapperClass}, new SqlHandler(resource));
    }
}
