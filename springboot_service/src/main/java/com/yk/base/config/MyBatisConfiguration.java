package com.yk.base.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.yk.base")
@AutoConfigureAfter(JdbcConfig.class)
public class MyBatisConfiguration {

    private Logger logger = LoggerFactory.getLogger("base");

    @Bean("sqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(HikariDataSource dataSource,
                                                  HikariDataSource primaryDataSource,
                                                  HikariDataSource anotherDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        // sqlSessionFactoryBean.setTypeHandlersPackage("com"); // 设置handler
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/**/*.xml"));
        sqlSessionFactoryBean.setTypeAliasesPackage("com.yk.demo.model");

        // variables作为全局变量, 可以在sql中直接用 '${xxx.xxx}' 直接获取到, 也可用于sql中的表达式(例如<if test="'${xxx.xxx}' == 'value'">)

        // 设置mybatis全局变量方式一(Configuration.variables)
        // buildSqlSessionFactory解析mybatis-config.xml后, 其中配置的中的<properties resource="xx.properties"/>会写入Configuration中的variable
        // sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));

        // 设置mybatis全局变量方式二(Configuration.variables)
        // Properties properties = new Properties();
        // try (InputStream input = new ClassPathResource("mybatis.properties").getInputStream()) {
        //     properties.load(input);
        // }
        // sqlSessionFactoryBean.setConfigurationProperties(properties);

        // 通过下面的方式可以注册mybatis的interceptor,
        // 在MyInterceptor中直接定义Properties全局变量(getter/setter),
        // mybatis-config.xml配置的<plugin>-<property>会把key-value设置到自定义的interceptor全局的Properties变量中去
        // sqlSessionFactoryBean.setPlugins(new MyInterceptor());
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

    /**
     * 通过构造函数注入
     */
    @Bean("commonTransactionManager")
    public PlatformTransactionManager commonTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
