package com.yk.test.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@MapperScan("com.yk.example.dao")
public class MybatisConfig {

    @Bean
    public DataSource getDataSource() {
        BeanConfig config = SpringContextUtil.getInstance().getBean("newBeanConfig", BeanConfig.class);
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(config.getDriver());
        druidDataSource.setUrl(config.getUrl());
        druidDataSource.setUsername(config.getUsername());
        druidDataSource.setPassword(config.getPassword());
        druidDataSource.setMaxActive(config.getMaxActive());
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(5);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setValidationQuery("SELECT 1");
        return druidDataSource;
    }


    private Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

        Resource[] mappers = new Resource[0];
        try {
            mappers = resourceResolver.getResources("classpath*:mapper/**/*.xml");
        } catch (IOException e) {
            // ignore
        }

        return mappers;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(resolveMapperLocations());
        sqlSessionFactoryBean.setTypeAliasesPackage("com.yk.test.example.model");
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
        mapperScannerConfigurer.setBasePackage("com.yk.test.example.dao");
        return mapperScannerConfigurer;
    }
}
