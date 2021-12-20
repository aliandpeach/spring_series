package com.yk.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@ConfigurationProperties(prefix = "spring.http.encoding")
@PropertySources({@PropertySource("classpath:http.properties")})
@Data
public class HttpEncodingProperties
{
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private Charset charset = DEFAULT_CHARSET;

    private boolean force = true;
}
