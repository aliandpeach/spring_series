package com.yk.connector.http;

import com.yk.core.IExecutorService;
import com.yk.core.Request;
import com.yk.core.Response;
import com.yk.exception.SdkException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Http请求客户端服务类
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/21 10:06
 */
public class HttpExecutorServiceImpl implements IExecutorService
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpExecutorServiceImpl.class);

    @Override
    public Response execute(Request request)
    {
        return invoke((HttpRequest) request);
    }

    @Override
    public String getType()
    {
        return "http";
    }

    private Response invoke(HttpRequest request)
    {

        try
        {
            HttpCommand command = HttpCommand.create(request);
            return invokeRequest(command);
        }
        catch (SdkException re)
        {
            LOG.error("invoke http request failed", re);
            throw re;
        }
        catch (Exception pe)
        {
            LOG.error("invoke http request error", pe);
            throw new SdkException(pe.getMessage(), pe);
        }
    }

    private Response invokeRequest(HttpCommand command)
    {
        int status = 200;
        Response httpResponse = new Response();
//        addSubscribes(command);
        try (CloseableHttpResponse response = command.execute())
        {
            status = response.getStatusLine().getStatusCode();
            List<Map<String, String>> headerList = Arrays.stream(response.getAllHeaders())
                    .map(h -> new HashMap<>(Collections.singletonMap(h.getName(), h.getValue()))).collect(Collectors.toList());
            Map<String, String> headers = headerList.stream().flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1));
            httpResponse.setHeaders(headers);
            httpResponse.status(status);
            if (null != command.getRequest().getHttpResponseHandler())
            {
                // 下载文件等非[application/json] 的http请求调用者可以自行获取InputStream解析
                command.getRequest().getHttpResponseHandler().handleHttpResponse(response);
                return httpResponse;
            }
            String content = "";
            try
            {
                HttpEntity httpEntity = response.getEntity();
                content = EntityUtils.toString(httpEntity);
                httpResponse.setHttpResult(content);
            }
            catch (Exception e)
            {
                LOG.error("parse from json error", e);
            }
        }
        catch (IOException e)
        {
            LOG.error("http execute error", e);
            throw new SdkException(e.getMessage(), e);
        }
        finally
        {
            command.getHttpRequestBase().releaseConnection();
        }
        if (null != command.getRequest().getListener()
                && command.getRequest().getListener() instanceof HttpExecutorListener)
        {
            // 用户自定义了回调函数则，http请求直接返回就可以了
            LOG.debug("response = {}", httpResponse);
            HttpExecutorListener listener = (HttpExecutorListener) command.getRequest().getListener();
            listener.onHttpFinishListener(httpResponse);
            return httpResponse;
        }
        if (command.getRequest().isAsync())
        {
            // 用户自定义了异步返回
            LOG.debug("response async = {}", httpResponse);
            return httpResponse;
        }
        if (status != 200)
        {
            LOG.error("response status = {}, response = {}", status, httpResponse);
            return httpResponse;
        }
        return httpResponse;
    }
}
