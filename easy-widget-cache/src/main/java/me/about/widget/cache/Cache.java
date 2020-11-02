package me.about.widget.cache;


import me.about.widget.cache.redis.Hash;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: hugo.zxh
 * @Date: 2020/11/02 11:27
 * @Description:
 */
public interface Cache {
    Hash hash(String key);
}
