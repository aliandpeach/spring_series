package com.yk.base.uitl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static SpringContext getInstance() {
        return ContextUtilHolder.SpringContext;
    }

    /**
     * 该bean在初始化的过程中，会通过该函数设置context
     *
     * @param applicationContext applicationContext
     * @throws BeansException BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.applicationContext = applicationContext;
    }

    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    private static class ContextUtilHolder {
        public static SpringContext SpringContext = applicationContext.getBean(SpringContext.class);
    }


}
