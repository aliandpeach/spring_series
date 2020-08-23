package com.yk.base.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.yk.base.util.DESUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@ConfigurationProperties(prefix = "jdbc")
@PropertySources({@PropertySource("classpath:druid.properties")})
@Data
public class JdbcConfig {
    private String driverClassName;

    private String url;

    private String username;

    private String password;

    @Bean
    public JdbcConfig newConfig() {
        return this;
    }

    @Bean("dataSource")
    public DataSource dataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(DESUtils.decryptString(username));
        dataSource.setPassword(DESUtils.decryptString(password));

        dataSource.setDbType("mysql");
        //最大连接池数量
        dataSource.setMaxActive(30);
        // 初始化时建立物理连接的个数
        dataSource.setInitialSize(5);
        // 最小连接池数量
        dataSource.setMinIdle(5);
        // 获取连接时最大等待时间，单位毫秒
        dataSource.setMaxWait(60000);
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        // 连接保持空闲而不被驱逐的最小时间
        dataSource.setMinEvictableIdleTimeMillis(300000);
        // 用来检测连接是否有效的sql，要求是一个查询语句
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        // 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        dataSource.setTestWhileIdle(true);
        // 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        dataSource.setTestOnBorrow(false);
        // 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        dataSource.setTestOnReturn(false);
        // 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。
        dataSource.setPoolPreparedStatements(false);
        // 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(0);
        // 配置监控统计拦截的filters，去掉后监控界面sql无法统计
        dataSource.setFilters("stat,wall");
        // 通过connectProperties属性来打开mergeSql功能；慢SQL记录
        dataSource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500");
        // 合并多个DruidDataSource的监控数据
        dataSource.setUseGlobalDataSourceStat(true);
        return dataSource;
    }
}
