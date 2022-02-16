package com.yk.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * StartUpListener
 *
 * @author yangk
 * @version 1.0
 * @since 2021/3/22 14:45
 */
@Component
public class ApplicationEventListener /*implements ApplicationListener<ApplicationReadyEvent>*/
{
    private Logger logger = LoggerFactory.getLogger("demo");

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent(ApplicationReadyEvent event)
    {
        logger.info("main onApplicationEvent starting " + System.currentTimeMillis());
        logger.info("main onApplicationEvent started " + System.currentTimeMillis());
    }
}
