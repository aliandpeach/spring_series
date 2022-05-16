package com.yk.base.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseHandler implements HandlerInterceptor
{
    private Logger logger = LoggerFactory.getLogger("demo");

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException
    {
        logger.info("请求之前处理");
        String token = request.getHeader("token");
        System.out.println("token=" + token);
        if (StringUtils.isEmpty(token))
        {
            try
            {
                response.getWriter().write("token is null");
            }
            catch (IOException e)
            {
                logger.error("BaseHandler preHandle error", e);
            }
            return true;
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
    {
        System.out.println("postHandle方法之后处理");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
    {
        System.out.println("afterCompletion方法之后处理");
    }
}
