package com.yk.connector.http;

import org.apache.http.HttpResponse;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/12/08 15:39:01
 */
public interface HttpResponseHandler
{
    void handleHttpResponse(HttpResponse response);
}
