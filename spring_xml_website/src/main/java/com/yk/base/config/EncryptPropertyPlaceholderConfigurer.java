package com.yk.base.config;

import com.yk.base.uitl.DESUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * PropertyPlaceholderConfigurer过期, 可替换为 PropertySourcesPlaceholderConfigurer
 *
 *
 *
 * // ConfigurationClassParser -> MutablePropertySources propertySources = ((ConfigurableEnvironment) this.environment).getPropertySources();
 * // 使用PropertySource(), 和PropertySourcesPlaceholderConfigurer一样都是放入environment变量
 * 使用 @org.springframework.context.annotation.PropertySource("classpath:application.properties")会覆盖PropertySourcesPlaceholderConfigurer
 * 因为二者都是放入environment
 */
public class EncryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private final String[] encryptProps = new String[]{"jdbc.username", "jdbc.password"};

    @Override
    protected String convertProperty(String propertyName, String propertyValue) {
        for (String prop : encryptProps) {
            if (propertyName.equals(prop)) {
                return convertPropertyValue(DESUtils.decryptString(propertyValue));
            }
        }
        return propertyValue;
    }
}
