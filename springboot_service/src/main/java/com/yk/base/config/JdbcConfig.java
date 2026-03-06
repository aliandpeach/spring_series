package com.yk.base.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ConfigurationProperties(prefix = "jdbc")
@PropertySources({@PropertySource("classpath:druid.properties"), @PropertySource("classpath:Hikari.properties")})
@Data
public class JdbcConfig {
    private String driverClassName;

    private String url;

    private String username;

    private String password;

    @Value("${druid.maxActive}")
    private int maxActive;

    @Bean
    public JdbcConfig newConfig() {
        return this;
    }

//    @Bean("dataSource2")
//    public DataSource dataSource2() throws SQLException {
//        // 也可以使用SpringBoot2提供的高性能数据库连接池
//        HikariDataSource hikariDataSource = new HikariDataSource();
//
//        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setDriverClassName(driverClassName);
//        dataSource.setUrl(url);
//        dataSource.setUsername(DESUtils.decryptString(username));
//        dataSource.setPassword(DESUtils.decryptString(password));
//
//        dataSource.setDbType("mysql");
//        //最大连接池数量
//        dataSource.setMaxActive(maxActive);
//        // 初始化时建立物理连接的个数
//        dataSource.setInitialSize(5);
//        // 最小连接池数量
//        dataSource.setMinIdle(5);
//        // 获取连接时最大等待时间，单位毫秒
//        dataSource.setMaxWait(60000);
//        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
//        dataSource.setTimeBetweenEvictionRunsMillis(60000);
//        // 连接保持空闲而不被驱逐的最小时间
//        dataSource.setMinEvictableIdleTimeMillis(300000);
//        // 用来检测连接是否有效的sql，要求是一个查询语句
//        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
//        // 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
//        dataSource.setTestWhileIdle(true);
//        // 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
//        dataSource.setTestOnBorrow(false);
//        // 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
//        dataSource.setTestOnReturn(false);
//        // 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。
//        dataSource.setPoolPreparedStatements(false);
//        // 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。
//        dataSource.setMaxPoolPreparedStatementPerConnectionSize(0);
//        // 配置监控统计拦截的filters，去掉后监控界面sql无法统计
//        dataSource.setFilters("stat,wall");
//        // 通过connectProperties属性来打开mergeSql功能；慢SQL记录
//        dataSource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500");
//        // 合并多个DruidDataSource的监控数据
//        dataSource.setUseGlobalDataSourceStat(true);
//        return dataSource;
//    }

    /**
     * 通过@ConfigurationProperties 让SpringBoot在方法返回 CustomerDataSourceProperties 空对象后,
     * 从配置文件中读取 spring.datasource.primary 前缀的属性，并通过setter或反射注入到对象中
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public CustomerDataSourceProperties primaryDataSourceProperties()
    {
        return new CustomerDataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.another")
    public CustomerDataSourceProperties anotherDataSourceProperties()
    {
        return new CustomerDataSourceProperties();
    }

    @Bean(name = "primaryDataSource")
    @Primary
    public HikariDataSource primaryDataSource(CustomerDataSourceProperties primaryDataSourceProperties)
    {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(primaryDataSourceProperties.getDriverClassName());
        dataSource.setJdbcUrl(primaryDataSourceProperties.getUrl());
        dataSource.setUsername(primaryDataSourceProperties.getUsername());
        dataSource.setPassword(primaryDataSourceProperties.getPassword());

        // 设置 Hikari 专属属性
        CustomerDataSourceProperties.HikariProperties hikari = primaryDataSourceProperties.getHikari();
        dataSource.setMaximumPoolSize(hikari.getMaximumPoolSize());
        dataSource.setMinimumIdle(hikari.getMinimumIdle());
        dataSource.setIdleTimeout(hikari.getIdleTimeout());
        dataSource.setConnectionTimeout(hikari.getConnectionTimeout());
        dataSource.setMaxLifetime(hikari.getMaxLifetime());
        dataSource.setConnectionTestQuery(hikari.getConnectionTestQuery());
        dataSource.setPoolName(hikari.getPoolName());
        dataSource.setAutoCommit(hikari.isAutoCommit());
        return dataSource;
    }

    @Bean(name = "anotherDataSource")
    public HikariDataSource anotherDataSource(CustomerDataSourceProperties anotherDataSourceProperties)
    {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(anotherDataSourceProperties.getDriverClassName());
        dataSource.setJdbcUrl(anotherDataSourceProperties.getUrl());
        dataSource.setUsername(anotherDataSourceProperties.getUsername());
        dataSource.setPassword(anotherDataSourceProperties.getPassword());

        // 设置 Hikari 专属属性
        CustomerDataSourceProperties.HikariProperties hikari = anotherDataSourceProperties.getHikari();
        dataSource.setMaximumPoolSize(hikari.getMaximumPoolSize());
        dataSource.setMinimumIdle(hikari.getMinimumIdle());
        dataSource.setIdleTimeout(hikari.getIdleTimeout());
        dataSource.setConnectionTimeout(hikari.getConnectionTimeout());
        dataSource.setMaxLifetime(hikari.getMaxLifetime());
        dataSource.setConnectionTestQuery(hikari.getConnectionTestQuery());
        dataSource.setPoolName(hikari.getPoolName());
        dataSource.setAutoCommit(hikari.isAutoCommit());
        return dataSource;
    }

    /**
     * 若有需要做动态数据源，在primary和another之间动态切换, 则该方法设置为@Primary(取消掉第一个数据源方法上的@Primary以及改bean名称为primaryDatasource),
     * 其他部分参数spring_cxf_service - dynamic目录中的部分代码
     */
    /*@Bean(name = "dataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource primaryDataSource, DataSource anotherDataSource)
    {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("PRIMARY", primaryDataSource);
        targetDataSources.put("ANOTHER", anotherDataSource);
        return new DynamicDataSource(primaryDataSource, targetDataSources);
    }*/
}
