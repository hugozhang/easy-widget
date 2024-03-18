package me.about.widget.mybatis.function.multidatasource;

/**
 * 多数据源线程绑定
 *
 * @author: hugo.zxh
 * @date: 2022/05/20 11:23
 * @description:
 */
public class DbContextHolder {

    /**
     * 初始化db1
     */
    private static final ThreadLocal<String> contextHolder = ThreadLocal.withInitial(DbTypeEnum.DB1::getValue);

    public static String getDefaultDbType() {
        return DbTypeEnum.DB1.getValue();
    }

    /**
     * 设置数据源
     * @param dbTypeEnum
     */
    public static void setDbType(DbTypeEnum dbTypeEnum) {
        contextHolder.set(dbTypeEnum.getValue());
    }

    /**
     * 取得当前数据源
     * @return
     */
    public static String getDbType() {
        return contextHolder.get();
    }

}
