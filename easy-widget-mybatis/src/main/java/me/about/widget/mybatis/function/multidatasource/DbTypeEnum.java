package me.about.widget.mybatis.function.multidatasource;

/**
 * 数据源枚举
 *
 * @author: hugo.zxh
 * @date: 2022/05/20 11:26
 * @description:
 */
public enum DbTypeEnum {

    /**
     * db1
     */
    DB1("db1"),
    /**
     * db2
     */
    DB2("db2"),
    /**
     * db3
     */
    DB3("db3"),
    /**
     * db4
     */
    DB4("db4"),
    /**
     * db5
     */
    DB5("db5");

    private String value;

    DbTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
