package com.yk.base.utils;

import com.yk.base.BaseMetadata;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestUtils
{
    public static final String REQUEST_BASE_METADATA = "REQUEST_BASE_METADATA";

    public static HttpServletRequest getRequest()
    {
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
    }

    public static HttpServletResponse getResponse()
    {
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
    }

    public static void setNewBaseMetadata()
    {
        getRequest().setAttribute(REQUEST_BASE_METADATA, new BaseMetadata());
    }

    public static BaseMetadata getBaseMetadata()
    {
        return (BaseMetadata) getRequest().getAttribute(REQUEST_BASE_METADATA);
    }

    public static void addRequestIp(String ip)
    {
        ((BaseMetadata) getRequest().getAttribute(REQUEST_BASE_METADATA)).setIp(ip);
    }
}
