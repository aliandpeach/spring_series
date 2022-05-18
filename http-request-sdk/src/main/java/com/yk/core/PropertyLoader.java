package com.yk.core;

import com.yk.exception.SdkException;
import com.yk.others.NotEmpty;
import com.yk.others.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 配置文件加载
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/25 10:13
 */
public class PropertyLoader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyLoader.class);

    public static CommonInfo loadProperties() throws IOException
    {
        String location = "sdk.properties";
        if (null != System.getProperty("sdk.location"))
        {
            location = System.getProperty("sdk.location");
        }
        if (null != System.getenv("SDK_LOCATION"))
        {
            location = System.getProperty("SDK_LOCATION");
        }
        CommonInfo CommonInfo = new CommonInfo();
        try (InputStream input = getInputStream(location))
        {
            Properties properties = new Properties();
            properties.load(input);
            Field[] fields = CommonInfo.class.getDeclaredFields();

            Map<String, Field> fieldValues = Arrays.stream(fields).collect(Collectors.toMap(t -> t.getAnnotation(Value.class).value(), t -> t, (k1, k2) -> k1));

            fieldValues.forEach((key, value) ->
            {
                try
                {
                    makeAccessible(value);
                    Class<?> clazz = value.getAnnotation(Value.class).type();
                    Class<?> fieldType = value.getType();
                    Object o = properties.get(key);
                    Annotation annotation = value.getAnnotation(NotEmpty.class);
                    if (annotation != null && (null == o || String.valueOf(o).trim().length() == 0))
                    {
                        LOGGER.error("配置资源信息" + key + "为空");
                        throw new SdkException("配置资源信息" + key + "为空");
                    }
                    switch (clazz.getName())
                    {
                        case "int":
                        case "java.lang.Integer":
                            value.set(CommonInfo, null == o ? 0 : Integer.parseInt(o.toString()));
                            break;
                        case "java.lang.Long":
                            value.set(CommonInfo, null == o ? 0L : Long.parseLong(o.toString()));
                            break;
                        case "java.lang.Boolean":
                            value.set(CommonInfo, null != o && Boolean.parseBoolean(o.toString()));
                            break;
                        default:
                            value.set(CommonInfo, o);
                            break;
                    }

                }
                catch (IllegalAccessException e)
                {
                    LOGGER.error("field {} set value error", key);
                }
            });
        }
        return CommonInfo;
    }

    public static InputStream getInputStream(String location) throws IOException
    {
        InputStream input = null;
        try
        {
            File sdk = new File(location);
            if (sdk.exists() && sdk.isFile())
            {
                input = new FileInputStream(sdk);
                return input;
            }
        }
        catch (Exception e)
        {
            LOGGER.error("load location " + location + " by new File() error {}", e.getMessage());
        }

        try
        {
            URL url = new URL(location);
            input = url.openStream();
            if (input != null)
            {
                return input;
            }
        }
        catch (IOException e)
        {
            LOGGER.error("load location " + location + " by new URL {}", e.getMessage());
        }

        input = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);

        if (input == null)
        {
            throw new IOException("无法加载资源文件: " + location);
        }
        return input;
    }

    protected static void makeAccessible(final Field field)
    {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()))
        {
            field.setAccessible(true);
        }
    }
}
