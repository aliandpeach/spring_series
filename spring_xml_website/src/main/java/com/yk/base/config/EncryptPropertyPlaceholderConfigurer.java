package com.yk.base.config;

import com.yk.base.uitl.DESUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * PropertyPlaceholderConfigurer过期, 可替换为 PropertySourcesPlaceholderConfigurer
 */
public class EncryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private String[] encryptProps = new String[]{"jdbc.username", "jdbc.password"};

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
