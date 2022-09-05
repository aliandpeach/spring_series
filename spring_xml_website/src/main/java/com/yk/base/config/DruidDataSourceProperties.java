package com.yk.base.config;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class DruidDataSourceProperties {

    @Value("${jdbc.driverClassName}")
    private String driverClassName;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.dbType}")
    private String dbType;

    @Value("${jdbc.maxActive}")
    private String maxActive;

    @Value("${validationQuery}")
    private String validationQuery;


    @Override
    public String toString() {
        /*return "DruidDataSource{" +
                "driverClassName='" + driverClassName + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", dbType='" + dbType + '\'' +
                ", maxActive='" + maxActive + '\'' +
                ", validationQuery='" + validationQuery + '\'' +
                '}';*/
        return ToStringBuilder.reflectionToString(this);
    }
}
