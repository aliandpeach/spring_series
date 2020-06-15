package com.yk.test.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceHolder extends PropertyPlaceholderConfigurer
{
    private static final String REGX_PLACE_HOLDER = "%\\{(.*)\\}";

    private static final Pattern REGX_PATTERN = Pattern.compile(REGX_PLACE_HOLDER);

    private String jdbcUrl = "jdbc.url";

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException
    {
        String url = props.getProperty(jdbcUrl);
        if (null != url)
        {
            url = replacePlaceholder("timeZone", url, timeZone());
            props.setProperty(jdbcUrl, url);
        }
        super.processProperties(beanFactoryToProcess, props);
    }

    private String replacePlaceholder(String placeholder, String string, String value)
    {
        Matcher matcher = REGX_PATTERN.matcher(string);
        while (matcher.find())
        {
            String placeholderString = matcher.group();
            string = string.replace(placeholderString, value);
        }
        return string;
    }

    private String timeZone()
    {
        TimeZone ttimezone = TimeZone.getDefault();
        return "UTC";
    }
}
