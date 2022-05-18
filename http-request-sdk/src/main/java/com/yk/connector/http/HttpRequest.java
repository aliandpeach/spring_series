package com.yk.connector.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.core.FileInfo;
import com.yk.core.JSONUtil;
import com.yk.core.Request;
import com.yk.core.SdkExecutors;
import com.yk.mq.MessageCenter;
import com.yk.mq.MessageTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * http请求参数组装
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/21 10:06
 */
public class HttpRequest extends Request
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

    private static final String UPLOADER_URI = "/SIMP_DBS_S/event/file/analysis/upload/json";

    private static final String ANALYZE_URI = "/SIMP_DBS_S/event/file/analysis/analyze";

    public static final String LOGIN_URI = "/SIMP_DBS_S/common/auth/acct/login";

    public static final String PRE_CHECK_JOB_STATUS_URI = "/SIMP_DBS_S/event/file/analysis/analyze/precheck";

    String host;

    String uri;

    String method;

    String contentType;

    Config config = Config.newConfig();

    Map<String, String> headers = new HashMap<>();

    Map<String, Object> params = new HashMap<>();

    boolean multipart;

    long timeout;

    private HttpResponseHandler httpResponseHandler;

    private Map<String, String> eventResult;

    public Map<String, String> getEventResult()
    {
        return eventResult;
    }

    public void setEventResult(Map<String, String> eventResult)
    {
        this.eventResult = eventResult;
    }

    private TypeReference typeReference;

    public HttpRequest()
    {
        super("http");
    }

    public TypeReference getTypeReference()
    {
        return typeReference;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public String getUri()
    {
        return uri;
    }

    public boolean isMultipart()
    {
        return multipart;
    }

    public String getContentType()
    {
        return contentType;
    }

    public Map<String, Object> getParams()
    {
        return params;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public Config getConfig()
    {
        return config;
    }

    public String getMethod()
    {
        return method;
    }

    public String getHost()
    {
        return host;
    }

    public HttpResponseHandler getHttpResponseHandler()
    {
        return httpResponseHandler;
    }

    public void setHttpResponseHandler(HttpResponseHandler httpResponseHandler)
    {
        this.httpResponseHandler = httpResponseHandler;
    }

    public static  HttpRequest.RequestBuilder create()
    {
        return new HttpRequest.RequestBuilder();
    }

    public static  HttpRequest.RequestBuilder analyze()
    {
        return new HttpRequest.RequestBuilder(ANALYZE_URI);
    }

    public static  HttpRequest.RequestBuilder uploader()
    {
        return new HttpRequest.RequestBuilder(UPLOADER_URI);
    }

    public static final class RequestBuilder
    {

        HttpRequest request;

        public RequestBuilder()
        {
            this.request = new HttpRequest();
            this.request.contentType = "application/json";
        }

        public RequestBuilder(String uri)
        {
            this.request = new HttpRequest();
            this.request.uri = uri;
            this.request.contentType = "application/json";
            switch (uri)
            {
                case ANALYZE_URI:
                    this.request.method = "GET";
                    this.request.multipart = false;
                    break;
                case UPLOADER_URI:
                    this.request.method = "POST";
                    this.request.multipart = true;
                    break;
                default:
                    break;
            }
        }

        /**
         * HTTP请求服务器host: http://ip:port, https://ip:port
         *
         * @param host host
         * @return RequestBuilder
         */
        public HttpRequest.RequestBuilder host(String host)
        {
            this.request.host = host;
            return this;
        }

        /**
         * 请求uri
         *
         * @param uri uri
         * @return RequestBuilder
         */
        public HttpRequest.RequestBuilder uri(String uri)
        {
            this.request.uri = uri;
            return this;
        }

        /**
         * HTTP请求的header
         *
         * @param headers headers
         * @return RequestBuilder
         */
        public HttpRequest.RequestBuilder headers(Map<String, String> headers)
        {
            if (null != headers)
            {
                this.request.headers = headers;
            }
            return this;
        }

        public HttpRequest.RequestBuilder multipart(boolean multipart)
        {
            this.request.multipart = multipart;
            return this;
        }

        /**
         * 请求的参数集合
         *
         * @param params 参数集合
         * @return RequestBuilder
         */
        public HttpRequest.RequestBuilder params(Map<String, Object> params)
        {
            if (null != params)
            {
                this.request.params.putAll(params);
            }
            return this;
        }

        /**
         * 设置HTTP请求的方式, SDK只支持 GET、POST两种
         * @param method GET/POST
         * @return RequestBuilder
         */
        public HttpRequest.RequestBuilder method(String method)
        {
            this.request.method = method;
            return this;
        }

        /**
         * 指定请求文件
         *
         * @param fileInfo 文件
         * @return RequestBuilder
         */
        public HttpRequest.RequestBuilder file(FileInfo fileInfo)
        {
            this.request.fileInfo = fileInfo;
            this.request.multipart = true;
            return this;
        }

        /**
         * 是否异步完成请求, 该参数设定后, http请求完成后立即返回
         *
         * @return RequestBuilder
         */
        public HttpRequest.RequestBuilder async()
        {
            this.request.async = true;
            return this;
        }

        /**
         * 代理设置: 1. 有用户名密码的代理 2. 无用户名密码的代理
         *
         * @param proxyHost 代理
         * @return RequestBuilder
         */
        public HttpRequest.RequestBuilder proxy(ProxyHost proxyHost)
        {
            this.request.config.withProxy(proxyHost);
            return this;
        }

        /**
         * 设置HTTP请求的响应超时时间, 指发送Http请求后, 服务器未能按照时间返回结果的超时时间
         *
         * @param readTimeout 单位-毫秒
         * @return RequestBuilder
         */
        public HttpRequest.RequestBuilder httpReadTimeout(int readTimeout)
        {
            this.request.config.withReadTimeout(readTimeout);
            return this;
        }

        public HttpRequest.RequestBuilder contentType(String contentType)
        {
            this.request.contentType = contentType;
            return this;
        }

        /**
         * 检测任务超时设置, 单位-毫秒, 设置后 从上传文件开始到检测任务结束过程中，如果时间超过设置的值, 将提前返回
         * async 设置了异步请求, 则该参数无效
         * listener 自定义了listener, 则该参数无效
         *
         * @param millis 位-毫秒
         * @return RequestBuilder
         */
        public HttpRequest.RequestBuilder timeout(long millis)
        {
            this.request.timeout = millis;
            return this;
        }

        public HttpRequest.RequestBuilder manager(MessageTaskManager manager)
        {
            this.request.taskManager = manager;
            return this;
        }

        public HttpRequest.RequestBuilder ofTypeReference(TypeReference typeReference)
        {
            this.request.typeReference = typeReference;
            return this;
        }

        /**
         * Builds the HttpRequest
         */
        public HttpRequest build()
        {
            if (null == this.request.host)
            {
            }

            LOGGER.debug("uri = {}, file-info = {}", request.getUri(), request.getFileInfo());
            if (!LOGIN_URI.equalsIgnoreCase(request.uri))
            {
            }
            if (null == request.config.getProxy() && !Objects.isNull(SdkExecutors.create().getCommonInfo())
                    && null != SdkExecutors.create().getCommonInfo().getProxyHost()
                    && SdkExecutors.create().getCommonInfo().getProxyPort() > 0)
            {
                request.config.withProxy(new ProxyHost(SdkExecutors.create().getCommonInfo().getProxyHost(), SdkExecutors.create().getCommonInfo().getProxyPort()));
            }
            if (null == request.config.getProxy() && !Objects.isNull(SdkExecutors.create().getCommonInfo())
                    && null != SdkExecutors.create().getCommonInfo().getProxyHost()
                    && SdkExecutors.create().getCommonInfo().getProxyPort() > 0
                    && null != SdkExecutors.create().getCommonInfo().getProxyUser()
                    && null != SdkExecutors.create().getCommonInfo().getProxyPasswd())
            {
                request.config.withProxy(new ProxyHost(SdkExecutors.create().getCommonInfo().getProxyHost(),
                        SdkExecutors.create().getCommonInfo().getProxyPort(),
                        SdkExecutors.create().getCommonInfo().getProxyUser(),
                        SdkExecutors.create().getCommonInfo().getProxyPasswd()));
            }
            request.config.withSSLVerificationEnabled();
            return request;
        }
    }
}
