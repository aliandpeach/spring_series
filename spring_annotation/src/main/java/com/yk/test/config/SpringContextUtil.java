package com.yk.test.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext context;

    public static SpringContextUtil getInstance() {
        return ContextUtilHolder.contextUtil;
    }

    /**
     * 该bean在初始化的过程中，会通过该函数设置context
     *
     * @param applicationContext applicationContext
     * @throws BeansException BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.context = applicationContext;
    }

    public <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    private static class ContextUtilHolder {
        public static SpringContextUtil contextUtil = context.getBean(SpringContextUtil.class);
    }
}
