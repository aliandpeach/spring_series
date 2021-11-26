package com.yk.base.filter;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class BaseFilter implements Filter
{
    private Logger logger = LoggerFactory.getLogger("base");

    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        logger.info("BaseFilter doFilter");

        if (false)
        {
            if (!StringUtils.startsWithIgnoreCase(((HttpServletRequest) request).getHeader("Content-Type"), "multipart/")
                    && "POST".equalsIgnoreCase(((HttpServletRequest) request).getMethod()))
            {

                // 普通的POST可以通过request.getInputStream()获取body流
                // multipart 请求会被 tomcat 中的 FileUploadBase 直接解析为 List<ApplicationPart>
                // 因此在之后通过request.getInputStream()获取不到流, 因为 ServletInputStream 只能获取一次不能被 reset
                // 接着Spring mvc 中通过 DispatcherServlet的 checkMultipart -> resolveMultipart 再去解析 List<ApplicationPart> 后传给Controller

                // 以上只是Springboot的整个流程的猜测，如果不使用Springboot使用servlet上传文件，是否也不能直接获取流呢 (embed-tomcat毕竟是springboot内置的 可能修改过)
                // 要测试的话可以使用cn.hutool.extra.servlet.ServletUtil解析 multipart/form-data的报文
                StandardServletMultipartResolver standardServletMultipartResolver = new StandardServletMultipartResolver();
                MultipartHttpServletRequest multipartRequest = standardServletMultipartResolver.resolveMultipart(((HttpServletRequest) request));
                chain.doFilter(new ContentCachingRequestWrapper((HttpServletRequest) request), response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public void destroy()
    {

    }
}
