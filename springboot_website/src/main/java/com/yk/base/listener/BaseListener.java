package com.yk.base.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class BaseListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger("base");

    public void contextInitialized(ServletContextEvent sce) {
        logger.info("BaseListener contextInitialized");
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
