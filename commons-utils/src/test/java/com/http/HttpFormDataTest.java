package com.http;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.httprequest.HttpClientUtil;
import com.yk.httprequest.HttpFormDataUtil;
import com.yk.httprequest.JSONUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * HttpFormDataTest -- 该测试类下所有的 postFormData方法都可替换为 postFormDataByHttpClient
 *
 * @author yangk
 * @version 1.0
 * @since 2021/4/28 16:50
 */

public class HttpFormDataTest
{
    /**
     * Content-Type: multipart/form-data; boundary=----WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * <p>
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * Content-Disposition: form-data; name="files1"; filename="1.txt"
     * Content-Type: text/plain
     * <p>
     * 111111
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * Content-Disposition: form-data; name="files2"; filename="2.txt"
     * Content-Type: text/plain
     * <p>
     * 222222
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * Content-Disposition: form-data; name="params"; filename="blob"
     * Content-Type: application/json
     * <p>
     * {"id":"1619604419314","fileName":"name1"}
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi--
     */
    @Test
    public void multipleUploadWithRequestPartParams() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/upload/multiple/request/part/params";

        JSONObject jsonObject = new JSONObject();
        String fileId = UUID.randomUUID().toString().replace("-", "");
        jsonObject.put(fileId, "env.txt");

        Map<String, String> filePathMap = new HashMap<>();
        filePathMap.put(fileId, "D:\\env.txt");

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        HttpFormDataUtil.BaseResponse response = HttpFormDataUtil.postFormData(url,
                filePathMap,
                Collections.singletonMap("params", jsonObject.toJSONString(0)),
                headers,
                proxyInfo,
                boundary,
                "application/json");
        System.out.println(response);

    }

    @Test
    public void multipleUploadValidatedItem() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/upload/multiple/validated/item";

        Map<String, String> nameWithContent = new HashMap<>();
        nameWithContent.put("name", "env.txt");
        nameWithContent.put("value", "env.txt");

        Map<String, String> filePathMap = new HashMap<>();
        String fileId = UUID.randomUUID().toString().replace("-", "");
        filePathMap.put(fileId, "D:\\env.txt");

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();

        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        HttpFormDataUtil.BaseResponse response = HttpFormDataUtil.postFormData(url,
                filePathMap,
                nameWithContent,
                headers,
                proxyInfo,
                boundary,
                "text/plain");
        System.out.println(response);

    }

    /**
     * 上传文件接口, 附带json格式的参数
     */
    @Test
    public void uploadMultipleRequestParams() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/upload/multiple/request/params";

        Map<String, String> filePathMap = new HashMap<>();
        String fileId = UUID.randomUUID().toString().replace("-", "");
        filePathMap.put(fileId, "D:\\env.txt");

        Map<String, String> nameWithContent = new HashMap<>();
        nameWithContent.put("_key", "aaa");
        nameWithContent.put("_value", "bbb");

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        HttpFormDataUtil.BaseResponse response = HttpFormDataUtil.postFormData(url,
                filePathMap, nameWithContent, headers, proxyInfo, boundary, "application/json");
        System.out.println(response);

    }
    /**
     * 上传文件接口, 附带json格式的参数
     */
    @Test
    public void multipleUploadRequestParamStringName() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/upload/multiple/request/param/name";

        Map<String, String> filePathMap = new HashMap<>();
        String fileId = UUID.randomUUID().toString().replace("-", "");
        filePathMap.put(fileId, "D:\\env.txt");

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        HttpFormDataUtil.BaseResponse response = HttpFormDataUtil
                .postFormData(url,
                        filePathMap,
                        Collections.singletonMap("level", "level-test-123"),
                        headers,
                        proxyInfo,
                        boundary,
                        "text/plain");
        System.out.println(response);

    }

    @Test
    public void uploadMultipleRequestPartItems() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/upload/multiple/request/part/items";

        Map<String, String> filePathMap = new HashMap<>();
        String fileId = UUID.randomUUID().toString().replace("-", "");
        filePathMap.put(fileId, "D:\\env.txt");

        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("name", "env.txt-1");
        map.put("value", "env.txt-1");
        list.add(map);
        Map<String, String> _map = new HashMap<>();
        _map.put("name", "env.txt-2");
        _map.put("value", "env.txt-2");
        list.add(_map);
        String str = JSONUtil.toJson(list);

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        HttpFormDataUtil.BaseResponse response = HttpFormDataUtil
                .postFormData(url, filePathMap, Collections.singletonMap("items", str), headers, proxyInfo, boundary, "application/json");
        System.out.println(response);

    }

    @Test
    public void multipleUploadRequestPartStringName() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/upload/multiple/request/part/string/name";

        Map<String, String> filePathMap = new HashMap<>();
        String fileId = UUID.randomUUID().toString().replace("-", "");
        filePathMap.put(fileId, "D:\\env.txt");

        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("name", "json-1");
        map.put("value", "json-1");
        list.add(map);
        Map<String, String> _map = new HashMap<>();
        _map.put("name", "json-2");
        _map.put("value", "json-2");
        list.add(_map);
        String str = JSONUtil.toJson(list);

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        HttpFormDataUtil.BaseResponse response = HttpFormDataUtil
                .postFormData(url, filePathMap, Collections.singletonMap("json", str), headers, proxyInfo, boundary, "text/plain");
        System.out.println(response);
    }

    @Test
    public void uploadBytes() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/upload/multiple/bytes";
        byte[] content = "abc123.中文".getBytes(StandardCharsets.UTF_8);
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        HttpFormDataUtil.BaseResponse response = HttpFormDataUtil.postBytes(url, new HashMap<>(), content, proxyInfo);
        System.out.println(response);
    }

    @Test
    public void uploadBytes2() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/upload/multiple/bytes";
        byte[] content = "abc123.中文".getBytes(StandardCharsets.UTF_8);
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        Object response = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(proxyInfo))
                .postBytes(url, new HashMap<>(),
                        content,
                        new HttpClientUtil.JsonResponseHandler<>(new TypeReference<Object>() {}));
        System.out.println(response);
    }

    @Test
    public void downloadBytes() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/download/bytes";
        new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")))
                .post(url,
                        new HashMap<>(),
                        cn.hutool.json.JSONUtil.toJsonStr(new HashMap<>(Collections.singletonMap("download.name", "123.mp4"))),
                        new ResponseHandler<byte[]>()
                        {
                            @Override
                            public byte[] handleResponse(HttpResponse response) throws IOException
                            {
                                return IOUtils.toByteArray(response.getEntity().getContent());
                            }
                        });
    }

    @Test
    public void download() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/download";
        new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")))
                .post(url,
                        new HashMap<>(),
                        cn.hutool.json.JSONUtil.toJsonStr(new HashMap<>(Collections.singletonMap("download.name", "123.mp4"))),
                        new ResponseHandler<Integer>()
                        {
                            @Override
                            public Integer handleResponse(HttpResponse response) throws IOException
                            {
                                IOUtils.copy(response.getEntity().getContent(), new FileOutputStream(new File("D:\\download\\123.mp4")));
                                return 0;
                            }
                        });
    }
}