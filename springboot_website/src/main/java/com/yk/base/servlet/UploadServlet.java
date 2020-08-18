package com.yk.base.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

public class UploadServlet extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger("base");

    @Override
    public void service(ServletRequest request, ServletResponse response) {
        logger.info("UploadServlet service");
    }
}
