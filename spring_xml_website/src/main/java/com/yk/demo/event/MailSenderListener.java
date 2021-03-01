package com.yk.demo.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MailSenderListener/* implements ApplicationListener<MailSendEvent>*/ {
    
    /*@Override*/
    @EventListener(MailSendEvent.class)
    public void onApplicationEvent(MailSendEvent event) {
        Optional.of(event).ifPresent((e) -> System.out.println(e.getTo()));
    }
}