package com.yk.base.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.yk.base.util.DESUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
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
@PropertySource("classpath:druid.properties")
@Order(3)
public class MybatisConfig {

    private Logger logger = LoggerFactory.getLogger(MybatisConfig.class);
    
    /**
     * 为什么无法注入
     */
    @Autowired
    @Qualifier("newBeanConfig")
    private BeanConfig newBeanConfig;

    private Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver1 = new PathMatchingResourcePatternResolver();
        Resource[] mappers = new Resource[0];
        try {
            mappers = resourceResolver1.getResources("classpath*:mapper/**/*.xml");
        } catch (IOException e) {
            logger.error("resolveMapperLocations errors", e);
        }
        return mappers;
    }
    
    @Bean("sqlSessionFactoryBean")
    public SqlSessionFactoryBean getSqlSessionFactoryBean(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        // sqlSessionFactoryBean.setTypeHandlersPackage(""); // 设置handler
        // 指定mapper.xml的位置
        sqlSessionFactoryBean.setMapperLocations(resolveMapperLocations());
        // 指定模型类
        sqlSessionFactoryBean.setTypeAliasesPackage("com.yk.demo");
        return sqlSessionFactoryBean;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(SqlSessionFactoryBean sqlSessionFactoryBean) throws Exception {
        return sqlSessionFactoryBean.getObject();
    }

    @Bean("sqlSessionTemplate")
    public SqlSessionTemplate getSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        return sqlSessionTemplate;
    }
    
    /**
     * MapperScannerConfigurer 作用等同于@MapperScan BasePackage就是配置于 mapper.xml中namespace的接口
     *
     * 使用接口模式必须定义MapperScannerConfigurer的Bean 或者使用@MapperScan指定 basePackage（接口所在的包路径）
     *
     * 注解@Mapper 是对单个接口类的使用, 自动生成接口实现类, 如果@MapperScan路径已经包含了某个接口, 则不需要在接口类上单独再加@Mapper
     *
     * @return
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionTemplateBeanName("sqlSessionTemplate");
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.yk.demo.**");
        /*  com.yk.demo.** Spring使用PathMatchingResourcePatternResolver解析, 然后找到接口的.class文件,利用反射生成bean */
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
