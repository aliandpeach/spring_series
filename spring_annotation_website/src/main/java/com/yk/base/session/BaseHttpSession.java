package com.yk.base.session;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class BaseHttpSession implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        se.getSession().setMaxInactiveInterval(1000);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

    }
}
