package com.yk.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * ProxyFilter
 */
@WebFilter(filterName = "proxyFilter", urlPatterns = {"/*"})
public class ProxyFilter implements Filter
{
    private static final Logger logger = LoggerFactory.getLogger(ProxyFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException
    {
        String uri = ((HttpServletRequest) request).getRequestURI();
        if (uri.endsWith(".jsp"))
        {
            logger.info(uri);
        }
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy()
    {
    
    }
}
