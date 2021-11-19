package com.yk.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/11 11:54:08
 */
@Configuration
public class JpaConfig
{
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource)
//    {
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setJpaVendorAdapter(vendorAdapter);
//        factory.setPackagesToScan("com.yk");
//        factory.setJpaProperties(hibernateProperties());
//        factory.setDataSource(dataSource);
//        return factory;
//    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource, EntityManagerFactory entityManagerFactory)
    {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setDataSource(dataSource);
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    private Properties hibernateProperties()
    {
        Properties properties = new Properties();
        // 显示sql语句
        properties.put("hibernate.show_sql", true);
        // 格式化sql语句
        properties.put("hibernate.format_sql", true);
        // 方言
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        // 自动生成表
        properties.put("hibernate.hbm2ddl.auto", "update");
        //设置事务提交模式
//        properties.put("hibernate.connection.autocommit",false);
//        properties.put("org.hibernate.flushMode", "COMMIT");
//        properties.put("hibernate.enable_lazy_load_no_trans",true);
        return properties;
    }
}
