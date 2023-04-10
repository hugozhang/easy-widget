package me.about.widget.multicache.annotation;

/**
 * 缓存操作
 *
 * @author: hugo.zxh
 * @date: 2022/06/24 15:52
 * @description:
 */
public enum MultiLevelTypeEnum {

    /**
     * get
     */
    GET("get"),
    /**
     * put
     */
    PUT("put"),
    /**
     * evict
     */
    EVICT("evict"),
    /**
     * in_query
     */
    IN_QUERY("in_query");

    private String value;

    MultiLevelTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
