package me.about.widget.cache;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 11:27
 * @Description:
 */
public interface CacheService {

    boolean hasKey(String key);

    Object get(String key);

    void put(String key,Object value);

    void put(String key, Object value, long expire, TimeUnit timeUnit);

    void delete(String key);

}
