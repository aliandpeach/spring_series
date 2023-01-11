package com.yk.base.filter;

import com.yk.base.utils.IPUtil;
import com.yk.base.utils.RequestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestMetadataFilter extends OncePerRequestFilter
{
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        RequestUtils.setNewBaseMetadata();
        RequestUtils.getBaseMetadata().setIp(IPUtil.getIpAddr(request));
        filterChain.doFilter(request, response);
    }
}
