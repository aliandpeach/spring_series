package com.yk.base.filter;

import com.yk.base.config.HttpEncodingProperties;
import com.yk.base.config.JdbcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableConfigurationProperties(HttpEncodingProperties.class)
@ConditionalOnClass(CharacterEncodingFilter.class)
@ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)
public class HttpEncodingAutoConfiguration {

    @Autowired
    private HttpEncodingProperties httpEncodingProperties;

    @Autowired
    private JdbcConfig jdbcConfig;

    @Bean
    @ConditionalOnMissingClass("characterEncodingFilter")
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
        filter.setEncoding(httpEncodingProperties.getCharset().name());
        filter.setForceEncoding(httpEncodingProperties.isForce());
        return filter;
    }
}
