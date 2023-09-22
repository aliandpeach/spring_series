package com.yk.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
    /**
     * 自动配置 {@link org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration} 不需要手动配置
     *         HibernateJpaAutoConfiguration -> HibernateJpaConfiguration -> JpaBaseConfiguration
     *
     *     JPA的配置方式
     *     <bean id="entityManagerFactory"
     *         class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
     *
     *         <property name="jpaVendorAdapter">
     *             <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
     *                 <property name="showSql" value="true" />
     *                 <property name="generateDdl" value="true" />
     *                 <property name="databasePlatform" value="${db.dialect}" />
     *             </bean>
     *         </property>
     *         <property name="jpaProperties">
     *             <props>
     *                 <!-- base -->
     *                 <prop key="hibernate.hbm2ddl.auto">update</prop>
     *                 <prop key="hibernate.transaction.flush_before_completion">true</prop>
     *                 <!-- show_sql -->
     *                 <prop key="hibernate.show_sql">false</prop>
     *                 <!-- cache -->
     *                 <prop key="hibernate.cache.use_query_cache">true</prop>
     *                 <prop key="hibernate.cache.use_second_level_cache">true</prop>
     *                 <prop key="hibernate.cache.provider_class">net.sf.ehcache.hibernate.EhCacheProvider</prop>
     *                 <prop key="hibernate.cache.region.factory_class">org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory
     *                 </prop>
     *             </props>
     *         </property>
     *     </bean>
     */
//    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource)
    {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.yk");
        factory.setJpaProperties(hibernateProperties());
        factory.setDataSource(dataSource);
        return factory;
    }

    /**
     * JPA的事务管理器 必须使用JpaTransactionManager 类生成
     */
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
