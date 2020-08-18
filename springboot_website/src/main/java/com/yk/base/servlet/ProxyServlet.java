package com.yk.base.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(urlPatterns = "/proxy")
public class ProxyServlet extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger("base");

    @Override
    public void service(ServletRequest request, ServletResponse response) {
        logger.info("ProxyServlet service");
    }
}
