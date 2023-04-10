package me.about.widget.mybatis.proxy;

import java.io.IOException;
import java.lang.reflect.Proxy;

/**
 * Mapper 代理
 *
 * @author: hugo.zxh
 * @date: 2022/02/10 15:24
 * @Description:
 */
public class MapperProxyFactory {
    public static    <T> T getMapper(Class<T> mapperClass, String resource ) throws IOException {
        T proxy= (T) Proxy.newProxyInstance(mapperClass.getClassLoader(),new Class[]{mapperClass}, new SqlHandler(resource));
        return proxy;
    }
}
