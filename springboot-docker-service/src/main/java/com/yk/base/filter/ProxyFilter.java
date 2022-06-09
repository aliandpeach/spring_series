package com.yk.base.filter;


import com.yk.base.exception.DockerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class ProxyFilter implements Filter
{
    private Logger logger = LoggerFactory.getLogger(ProxyFilter.class);

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        logger.info("ProxyFilter doFilter");
        chain.doFilter(request, response);

        // 由handlerExceptionResolver处理异常, 但是返回的是ModelAndView
        /*handlerExceptionResolver.resolveException((HttpServletRequest) request,
                (HttpServletResponse) response,
                null,
                new DockerException("test proxy filter throw customer exception", 402));*/
        // 抛出异常由DockerErrorController统一处理
//        throw new DockerException("test proxy filter throw customer exception", 402);
    }

    public void destroy()
    {

    }
}

