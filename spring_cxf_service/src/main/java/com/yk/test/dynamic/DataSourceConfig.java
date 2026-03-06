package com.yk.test.dynamic;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kairou Zeng
 */
@Slf4j
@Configuration
@PropertySource(value = "classpath:jdbc.properties")
public class DataSourceConfig {

    /**
     * firstDataSource/secondDataSource在xml中配置
     */
    /*@Bean(name = "firstDataSource")
    public DataSource getFirstDataSource() {
    }

    @Bean(name = "secondDataSource")
    public DataSource getSecondDataSource() {
    }*/

    /**
     * DynamicDataSource在xml中配置
     */
    /*@Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource setDataSource(@Qualifier("firstDataSource") DataSource firstDataSource,
                                           @Qualifier("secondDataSource") DataSource secondDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DatabaseType.FIRST_MYSQL, firstDataSource);
        targetDataSources.put(DatabaseType.SECOND_MYSQL, secondDataSource);

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(firstDataSource);

        log.info("Get dynamic Datasource, target datasource is : {}", targetDataSources);
        return dynamicDataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:mybatis/mappers/*.xml"));

        log.info("SqlSessionFactoryBean!");
        return sqlSessionFactoryBean.getObject();
    }*/
}
