package me.about.widget.cache.core;

import lombok.Getter;

/**
 * 缓存操作
 *
 * @author: hugo.zxh
 * @date: 2022/06/24 15:52
 * @description:
 */
@Getter
public enum CacheOp {

    /**
     * get
     */
    GET("get"),
    /**
     * put
     */
    PUT("put"),
    /**
     * remove
     */
    REMOVE("remove"),
    /**
     * in_query
     */
    IN_QUERY("in_query");

    private final String value;

    CacheOp(String value) {
        this.value = value;
    }

}
