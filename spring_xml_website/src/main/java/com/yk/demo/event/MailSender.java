package com.yk.demo.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class MailSender implements ApplicationContextAware, InitializingBean {
    private ApplicationContext ac;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;

    }

    public void send() {
        MailSendEvent mse = new MailSendEvent(ac, "MailSender.");
        ac.publishEvent(mse);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.send();
    }
}
