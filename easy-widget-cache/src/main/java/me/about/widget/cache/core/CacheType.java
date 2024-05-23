package me.about.widget.cache.core;

import lombok.Getter;

/**
 * 缓存类型
 *
 * @author: hugo.zxh
 * @date: 2022/06/24 15:52
 * @description:
 */
@Getter
public enum CacheType {

    /**
     * local
     */
    LOCAL("local"),
    /**
     * remote
     */
    REMOTE("remote"),
    /**
     * both
     */
    BOTH("both");

    private final String value;

    CacheType(String value) {
        this.value = value;
    }

}
