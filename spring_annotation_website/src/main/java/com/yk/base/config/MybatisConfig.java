package com.yk.base.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.yk.base.util.DESUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;


@Component
@MapperScan("com.yk.demo")
@ComponentScan("com.yk")
@PropertySource("classpath:druid.properties")
@Order(3)
public class MybatisConfig {

    private Logger logger = LoggerFactory.getLogger("demo");

    private Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver1 = new PathMatchingResourcePatternResolver();
        Resource[] mappers = new Resource[0];
        try {
            mappers = resourceResolver1.getResources("classpath*:mappers/**/*.xml");
        } catch (IOException e) {
            logger.error("resolveMapperLocations errors", e);
        }
        return mappers;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(resolveMapperLocations());
        sqlSessionFactoryBean.setTypeAliasesPackage("com.yk.demo");
        return sqlSessionFactoryBean.getObject();
    }

    @Bean("sqlSessionTemplate")
    public SqlSessionTemplate getSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        return sqlSessionTemplate;
    }

    @Bean
    public MapperScannerConfigurer getMapperScannerConfigurer() throws Exception {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionTemplateBeanName("sqlSessionTemplate");
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.yk.base");
        return mapperScannerConfigurer;
    }

    @Bean("dataSource")
    public DataSource dataSource() {
        BeanConfig beanConfig = SpringContext.getInstance().getBean("newBeanConfig", BeanConfig.class);
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(beanConfig.getDriver());
        druidDataSource.setUrl(beanConfig.getUrl());
        druidDataSource.setUsername(DESUtils.decryptString(beanConfig.getUsername()));
        druidDataSource.setPassword(DESUtils.decryptString(beanConfig.getPassword()));
        druidDataSource.setMaxActive(beanConfig.getMaxActive());
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(5);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setValidationQuery("SELECT 1");
        return druidDataSource;
    }

    /**
     * 通过构造函数注入
     *
     * @param dataSource
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigure() {
        PropertySourcesPlaceholderConfigurer source = new PropertySourcesPlaceholderConfigurer();
//        source.setLocation(new ClassPathResource("config/env.properties"));
        return source;
    }
}
