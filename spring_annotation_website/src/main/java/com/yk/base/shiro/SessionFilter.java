package com.yk.base.shiro;

import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class SessionFilter extends UserFilter
{
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException
    {
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("{\"message\": \"请登录\"}");
    }
}
