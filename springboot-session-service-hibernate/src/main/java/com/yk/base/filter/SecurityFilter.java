package com.yk.base.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class SecurityFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger("base");

    public void init(FilterConfig filterConfig) {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("SecurityFilter doFilter");
        chain.doFilter(request, response);
//        throw new CustomException("test throw custom exception ", HttpStatus.PAYMENT_REQUIRED);
    }

    public void destroy() {

    }
}
