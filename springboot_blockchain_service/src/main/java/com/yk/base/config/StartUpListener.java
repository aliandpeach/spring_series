package com.yk.base.config;

import com.yk.bitcoin.KeyGeneratorWatchedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StartUpListener /*implements ApplicationListener<ApplicationReadyEvent>*/
{
    private static final Logger logger = LoggerFactory.getLogger(StartUpListener.class);
    
    @Autowired
    private KeyGeneratorWatchedService keyGeneratorWatchedService;
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent(ApplicationReadyEvent event)
    {
        logger.info("main onApplicationEvent starting " + System.currentTimeMillis());
//        keyGeneratorWatchedService.main();
        logger.info("main onApplicationEvent started " + System.currentTimeMillis());
    }
}
