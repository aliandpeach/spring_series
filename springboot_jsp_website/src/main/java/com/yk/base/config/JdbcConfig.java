package com.yk.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * JdbcConfig
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true) // <tx:annotation-driven transaction-manager="transactionManager" />
public class JdbcConfig
{
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource)
    {
        return new JdbcTemplate(dataSource);
    }
    
    /**
     * 装配事务管理器
     */
    @Bean
    public PlatformTransactionManager transactionManager(@Autowired DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }
}
