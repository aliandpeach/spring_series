package com.yk.base.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
@EnableTransactionManagement(proxyTargetClass = true)
// 配置切面事务, 若不配置切面事务, 就要给操作数据库的方法上配置@Transactional否则产生异常: Could not obtain transaction-synchronized Session for current thread
@ImportResource({ "classpath:transaction-management.xml" })
public class JpaConfig
{
    /**
     * SessionFactory继承自 EntityManagerFactory, 所以JPA自动配置了EntityManagerFactory, 就不要重复配置hibernate的sessionFactoryBean了
     * 非JPA的配置方式
     * <bean id="sessionFactory" class="org.springframework.orm.hibernate4.LocalSessionFactoryBean">
     *     <!-- 配置数据源属性 -->
     *     <property name="dataSource" ref="dataSource"></property>
     *     <!-- 配置 hibernate 配置文件的位置及名称 -->
     *     <!--
     *     <property name="configLocation" value="classpath:hibernate.cfg.xml"></property>
     *     -->
     *     <!-- 使用 hibernateProperties 属相来配置 Hibernate 原生的属性 -->
     *     <property name="hibernateProperties">
     *         <props>
     *             <prop key="hibernate.dialect">org.hibernate.dialect.MySQL5InnoDBDialect</prop>
     *             <prop key="hibernate.show_sql">true</prop>
     *             <prop key="hibernate.format_sql">true</prop>
     *              <prop key="hibernate.hbm2ddl.auto">update</prop>
     *         </props>
     *     </property>
     *     <!-- 配置 hibernate 映射文件的位置及名称, 可以使用通配符 -->
     *     <property name="mappingLocations" value="classpath:com/atguigu/spring/hibernate/entities/*.hbm.xml">
     *     </property>
     *     <!-- 配置 hibernate 实体Bean的映射，如果缺少此项内容则不会创建表，创建表的过程是在生成容器的时候同时生成表 -->
     *     <和上边配置xml类型的实体bean二选一就可以了>
     *     <property name="annotatedClasses">
     *         <list>
     *         <value>com.marshallee.entities.Account</value>
     *         </list>
     *     </property>
     * </bean>
     */
    @Bean
    public LocalSessionFactoryBean sessionFactoryBean(DataSource dataSource)
    {
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(dataSource);
        localSessionFactoryBean.setHibernateProperties(hibernateProperties());
        // 重要配置, 未配置会在查询中报 xxx is not mapped
        localSessionFactoryBean.setPackagesToScan("com.yk.db.jpa.model");
        return localSessionFactoryBean;
    }

    @Bean
    public HibernateTemplate hibernateTemplate(SessionFactory sessionFactory)
    {
        HibernateTemplate hibernateTemplate = new HibernateTemplate();
        hibernateTemplate.setSessionFactory(sessionFactory);
        return hibernateTemplate;
    }

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory)
    {
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
        hibernateTransactionManager.setSessionFactory(sessionFactory);
        return hibernateTransactionManager;
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
