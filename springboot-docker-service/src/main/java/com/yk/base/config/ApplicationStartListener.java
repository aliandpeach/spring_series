package com.yk.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * StartUpListener
 *
 * @author yangk
 * @version 1.0
 * @since 2021/3/22 14:45
 */
@Component
public class ApplicationStartListener implements ServletContextInitializer
{
    private static final Logger logger = LoggerFactory.getLogger("demo");
    private static final Logger error = LoggerFactory.getLogger("error");

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Override
    public void onStartup(ServletContext servletContext)
    {
        logger.info("main onStartup starting " + System.currentTimeMillis());
        AtomicInteger integer = new AtomicInteger(0);
        ScheduledExecutorService mainScheduled = Executors.newScheduledThreadPool(1, r -> new Thread(r, "main-listener-" + integer.getAndIncrement()));
        mainScheduled.scheduleWithFixedDelay(() ->
        {
            logger.info("spring docker service running \n" + dataSourceProperties.getUrl());
            error.error("spring docker service error \n" + dataSourceProperties.getUrl());
            error.info("spring docker service error \n" + dataSourceProperties.getUrl());
        }, 0, 3, TimeUnit.SECONDS);
        logger.info("main onStartup started " + System.currentTimeMillis());
    }
}
