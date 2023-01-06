package com.yk.base.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * 自定义Jackson反序列化日期类型时应用的类型转换器,一般用于@RequestBody接受参数时使用
 * <p>
 * 解决前端传递的字符串日期格式, 转换到后端Date后丢失时间问题(验证)
 * <p>
 * 注解配置方式
 */
@Component
public class DateJacksonConverter extends JsonDeserializer<Date>
{

    private static final String[] pattern = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.S", "yyyy.MM.dd", "yyyy.MM.dd HH:mm", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss.S", "yyyy/MM/dd", "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss.S"};

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        Date targetDate = null;
        String originDate = p.getText();
        if (StringUtils.isNotEmpty(originDate))
        {
            return null;
        }
        try
        {
            long longDate = Long.parseLong(originDate.trim());
            targetDate = new Date(longDate);
        }
        catch (NumberFormatException e)
        {
            try
            {
                targetDate = DateUtils.parseDate(originDate, DateJacksonConverter.pattern);
            }
            catch (ParseException pe)
            {
                throw new IOException(String.format("'%s' can not convert to type 'java.util.Date',just support timestamp(type of long) and following date format(%s)",
                        originDate,
                        StringUtils.join(pattern, ",")));
            }
        }
        return targetDate;
    }

    @Override
    public Class<?> handledType()
    {
        return Date.class;
    }
}