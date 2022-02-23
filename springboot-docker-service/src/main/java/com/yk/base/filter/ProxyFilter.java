package com.yk.base.filter;


import com.yk.base.exception.DockerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class ProxyFilter implements Filter
{
    private Logger logger = LoggerFactory.getLogger("demo");

    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        logger.info("ProxyFilter doFilter");
//        chain.doFilter(request, response);
        throw new DockerException("test proxy filter throw customer exception", 402);
    }

    public void destroy()
    {

    }
}

