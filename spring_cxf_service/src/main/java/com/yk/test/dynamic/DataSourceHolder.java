package com.yk.test.dynamic;

/**
 * 获取和设置数据源的类
 *
 * @author Kairou Zeng
 */
public class DataSourceHolder {

    /**
     * 本地线程
     */
    private static final ThreadLocal<DatabaseType> DATABASE_TYPE = new ThreadLocal<>();

    /**
     * 获取数据源
     *
     * @return 若本地线程为空，则返回默认数据源类型
     */
    public static DatabaseType getDatabaseType() {
        return DATABASE_TYPE.get() == null ? DatabaseType.FIRST_MYSQL : DATABASE_TYPE.get();
    }

    /**
     * 设置数据源
     *
     * @param databaseType 多源数据源
     */
    public static void setDatabaseType(DatabaseType databaseType) {
        if (null == databaseType) {
            throw new NullPointerException("DATABASE_TYPE is null");
        }
        DATABASE_TYPE.set(databaseType);
    }

    /**
     * 清除数据源
     */
    public static void clearDatabaseType() {
        DATABASE_TYPE.remove();
    }
}
