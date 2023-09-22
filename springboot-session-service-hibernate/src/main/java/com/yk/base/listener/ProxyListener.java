package com.yk.base.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ProxyListener implements ServletContextListener {
    private Logger logger = LoggerFactory.getLogger("base");

    public void contextInitialized(ServletContextEvent sce) {
        logger.info("ProxyListener contextInitialized");
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
