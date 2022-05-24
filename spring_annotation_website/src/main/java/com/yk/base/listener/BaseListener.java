package com.yk.base.listener;

import com.yk.demo.dao.IRoleDAO;
import com.yk.demo.dao.impl.RoleQueryDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BaseListener /*implements ApplicationListener<ContextRefreshedEvent>*/
{
    private static final Logger logger = LoggerFactory.getLogger(BaseListener.class);
    
    private AtomicInteger integer = new AtomicInteger(0);
    
    @Autowired
    private IRoleDAO roleDAO;
    
    @Autowired
    private RoleQueryDAO roleQueryDAO;
    
    /**
     * 当一个ApplicationContext被初始化或刷新触发
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        if (integer.incrementAndGet() > 1)
        {
            return;
        }
        logger.info("spring容器初始化完毕================================================");
        List<Map<String, Object>> list = roleDAO.queryRoles();
        list = new ArrayList<>();
        list = roleQueryDAO.queryRoles();
        logger.info("" + list);
    }
}
