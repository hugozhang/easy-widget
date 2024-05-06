package me.about.widget.spring.mvc.security;

import java.io.Serializable;

public interface SessionUser extends Serializable {
    /**
     * 获取用户索引名称  要唯一  用于区分用户
     */
    default String getIndexName() {
        return null;
    }
}
