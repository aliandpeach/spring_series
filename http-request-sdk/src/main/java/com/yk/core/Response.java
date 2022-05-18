package com.yk.core;

import java.util.Map;

/**
 * 请求回显
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/23 10:13
 */
public class Response
{
    private int status;

    private String httpResult;

    private Map<String, String> eventResult;

    private Map<String, String> headers;

    public int getStatus()
    {
        return status;
    }

    public Response status(int status)
    {
        this.status = status;
        return this;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public void setHeaders(Map<String, String> headers)
    {
        this.headers = headers;
    }

    public String getHttpResult()
    {
        return httpResult;
    }

    public void setHttpResult(String httpResult)
    {
        this.httpResult = httpResult;
    }

    public Map<String, String> getEventResult()
    {
        return eventResult;
    }

    public void setEventResult(Map<String, String> eventResult)
    {
        this.eventResult = eventResult;
    }

    @Override
    public String toString()
    {
        return "Response{" +
                "httpResult=" + httpResult +
                '}';
    }
}
