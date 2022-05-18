package com.yk.connector.http;

import cn.hutool.json.JSONUtil;
import com.yk.exception.SdkException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MinimalField;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 组装HTTP报文
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/21 10:06
 */
public final class HttpCommand
{
    private static final long MAX = 10 * 1024 * 1024L;

    private final HttpRequest request;

    private CloseableHttpClient client;

    HttpRequestBase httpRequestBase;

    private HttpCommand(HttpRequest request)
    {
        this.request = request;
    }

    public static HttpCommand create(HttpRequest request)
    {
        HttpCommand command = new HttpCommand(request);
        command.initialize();
        return command;
    }

    private void initialize()
    {
        URI url;
        try
        {
            url = params(request);
        }
        catch (URISyntaxException e)
        {
            throw new SdkException(e.getMessage(), e.getIndex(), e);
        }
        client = HttpClientFactory.INSTANCE.getClient(request.getConfig());

        switch (request.getMethod().toUpperCase(Locale.ENGLISH))
        {
            case "POST":
                httpRequestBase = new HttpPost(url);
                break;
            case "GET":
                httpRequestBase = new HttpGet(url);
                break;
            default:
                throw new SdkException("Unsupported http method: " + request.getMethod());
        }

        headers(request);

        if (request.isMultipart())
        {
            if (null == request.getFileInfo())
            {
                throw new SdkException("文件不能为空");
            }
            if (null == request.getFileInfo().getName() || null == request.getFileInfo().getPath())
            {
                throw new SdkException("文件名或者文件路径不能为空");
            }
            File upload = new File(request.getFileInfo().getPath());
            if (!upload.exists() || !upload.isFile())
            {
                throw new SdkException("文件名不存在或者不是文件");
            }

            String limitObj = System.getProperty("limit");
            if (null == limitObj || !limitObj.equalsIgnoreCase("false"))
            {
                if (upload.length() > MAX)
                {
                    throw new SdkException("不能上传大于10M的文件 : " + request.getFileInfo().getPath());
                }
            }
            request.getFileInfo().setSize(upload.length());
            request.getFileInfo().setModifyDate(upload.lastModified());

            try
            {
                BasicFileAttributes attributes = Files.readAttributes(upload.toPath(), BasicFileAttributes.class);
                long milliseconds = attributes.creationTime().to(TimeUnit.MILLISECONDS);
                request.getFileInfo().setCreateDate(milliseconds);
            }
            catch (IOException exception)
            {
                throw new SdkException("文件创建时间获取失败 : " + request.getFileInfo().getPath(), exception);
            }

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            String boundary = UUID.randomUUID().toString().replace("-", "");
            try
            {
                Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("org.apache.http.entity.mime.FormBodyPart");
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                Constructor<?> constructor = Arrays.stream(constructors).filter(c -> c.getParameterCount() == 3).findFirst().orElseThrow(() -> new SdkException("Constructor<?> null"));
                constructor.setAccessible(true);
                Header header = new Header();
                header.addField(new MinimalField(MIME.CONTENT_DISPOSITION, "form-data; name=\"params\"\r\nContent-Type: " + request.getContentType()));
                header.addField(new MinimalField(MIME.CONTENT_TYPE, request.getContentType()));
                header.addField(new MinimalField(MIME.CONTENT_TRANSFER_ENC, "binary"));
                FormBodyPart bodyPart = (FormBodyPart) constructor.newInstance("params", new ByteArrayBody(JSONUtil.toJsonStr(request.getFileInfo()).getBytes(StandardCharsets.UTF_8), ContentType.create(request.getContentType()), null), header);
                builder.addPart(bodyPart);
            }
            catch (ReflectiveOperationException e)
            {
                throw new SdkException("初始化文件上传报文失败", e);
            }

            try
            {
                InputStream input = new FileInputStream(upload);
                builder.addBinaryBody("file", input, ContentType.create("application/octet-stream"), upload.getName());
                builder.setCharset(StandardCharsets.UTF_8);
                builder.setContentType(ContentType.MULTIPART_FORM_DATA);
                builder.setBoundary(boundary);
            }
            catch (FileNotFoundException e)
            {
                throw new SdkException("文件不存在或者不是文件 : " + request.getFileInfo().getPath(), e);
            }
            ((HttpPost) httpRequestBase).setEntity(builder.build());
        }
        else if (httpRequestBase instanceof HttpPost)
        {
            EntityBuilder builder = EntityBuilder.create();
            builder.setText(JSONUtil.toJsonStr(request.getParams()));
            builder.setContentType(ContentType.APPLICATION_JSON);
            builder.setContentEncoding(StandardCharsets.UTF_8.name());
            ((HttpPost) httpRequestBase).setEntity(builder.build());
        }
    }

    public CloseableHttpResponse execute() throws IOException
    {
        return client.execute(httpRequestBase);
    }

    private URI params(HttpRequest request) throws URISyntaxException
    {
        URIBuilder uri = new URIBuilder(request.getHost() + request.getUri());
        if (!request.getMethod().equalsIgnoreCase("GET"))
        {
            return uri.build();
        }

        for (Map.Entry<String, Object> entry : request.getParams().entrySet())
        {
            uri.addParameter(entry.getKey(), entry.getValue() + "");
        }
        return uri.build();
    }

    private void headers(HttpRequest request)
    {
        for (Map.Entry<String, String> h : request.getHeaders().entrySet())
        {
            httpRequestBase.addHeader(h.getKey(), String.valueOf(h.getValue()));
        }
    }

    public HttpRequest getRequest()
    {
        return request;
    }

    public HttpRequestBase getHttpRequestBase()
    {
        return httpRequestBase;
    }
}
