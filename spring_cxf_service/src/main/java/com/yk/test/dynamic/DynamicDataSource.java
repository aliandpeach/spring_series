package com.yk.test.dynamic;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源 (适用于多个固定数据源的场景)
 * 对于注解了@Dynamic2(value = DatabaseType.FIRST_MYSQL)的方法, 就会默认使用FIRST_MYSQL库
 */
public class DynamicDataSource extends AbstractRoutingDataSource {


    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getDatabaseType();
    }
}
