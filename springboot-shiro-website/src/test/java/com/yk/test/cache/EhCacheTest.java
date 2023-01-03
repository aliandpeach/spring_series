package com.yk.test.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EhCacheTest
{
    @Test
    public void test() throws IOException, ParseException
    {
        long start = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-10-17 00:00:01")).getTime();
        long end = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-10-17 23:59:59")).getTime();
        System.out.println(start);
        System.out.println(end);
        System.out.println(new Date(1649845494000L));

//        InputStream is = this.getClass().getClassLoader().getResourceAsStream("spring-ehcache.xml");
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("spring-ehcache.xml");
        CacheManager cacheManager = new CacheManager(is);
        is.close();
        Cache cache = cacheManager.getCache("tokenCache");
        cache.put(new Element("abc", "123"));
        cache.flush();
        Element element = cache.get("abc");
        System.out.println(element);
        cache.flush();
    }

    @Test
    public void get() throws IOException
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("spring-ehcache.xml");
        CacheManager cacheManager = new CacheManager(is);
        is.close();
        Cache cache = cacheManager.getCache("tokenCache");
        Element element = cache.get("abc");
        System.out.println(element);
    }
}
