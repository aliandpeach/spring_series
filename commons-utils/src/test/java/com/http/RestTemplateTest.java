package com.http;

import com.yk.httprequest.HttpClientUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RestTemplateTest
{
    private RestTemplate restTemplate;

    @Before
    public void before()
    {
        HttpComponentsClientHttpRequestFactory httpRequestFactory;
        try
        {
            HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
            // 使用 http-client组件
            httpRequestFactory = new HttpComponentsClientHttpRequestFactory(new HttpClientUtil(new HttpClientUtil.Config().ofProxy(proxyInfo)).httpClient);
            httpRequestFactory.setConnectTimeout(150000);
            httpRequestFactory.setReadTimeout(50000);
        }
        catch (Exception e)
        {
            httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpRequestFactory.setConnectTimeout(15000);
            httpRequestFactory.setReadTimeout(5000);
        }

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        List<HttpMessageConverter<?>> converterList = new ArrayList<>();

        // FormHttpMessageConverter可读取HttpEntity<MultiValueMap<String, Object>>的内容参数为form-data报文格式
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        // 下面一行配置, FormHttpMessageConverter就可解析MultiValueMap, 使FormHttpMessageConverter支持application/octet-stream格式;
        // 在 RestTemplate$HttpEntityRequestCallback.doWithRequest, 就可以正常读取文件流类型
//        formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);

        // 下面两行的配置, FormHttpMessageConverter就可以解析对象参数, 使其转为json格式
        formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_JSON);
        formHttpMessageConverter.addPartConverter(new MappingJackson2HttpMessageConverter());
        converterList.add(formHttpMessageConverter);

        converterList.add(new MappingJackson2HttpMessageConverter());
        converterList.add(new StringHttpMessageConverter());
        converterList.add(new ByteArrayHttpMessageConverter());
        restTemplate.setMessageConverters(converterList);
        this.restTemplate = restTemplate;
    }

    @Test
    public void queryTest() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/query";

        Map<String, String> body = new HashMap<>();
        ResponseEntity<Object> infos = restTemplate.getForEntity(url, Object.class);
        System.out.println(infos);
        Object _infos = restTemplate.getForObject(url, Object.class);
        System.out.println(_infos);
    }

    static class MultipartInputStreamFileResource extends InputStreamResource
    {

        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream, String filename)
        {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename()
        {
            return this.filename;
        }

        @Override
        public long contentLength()
        {
            return -1; // we do not want to generally read the whole stream into memory ...
        }
    }

    @Test
    public void uploadTest() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/upload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Connection", "keep-alive");
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("upload_file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> infos = restTemplate.postForEntity(url, entity, String.class);
        System.out.println(infos);
    }

    @Test
    public void transfer0Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/0";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        Map<String, String> indexModel = new HashMap<>();
        indexModel.put("id", "123");
        indexModel.put("name", "456");
        map.add("indexModel", indexModel);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<byte[]> infos = restTemplate.postForEntity(url, entity, byte[].class);
        System.out.println(new File("D:\\env.txt").length() == Objects.requireNonNull(infos.getBody()).length);


        // 1.2 该方式直接指定ByteArrayHttpMessageConverter 不需要目标接口设置响应头
        map.remove("file");
        map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        byte[] _result = restTemplate.execute("https://192.168.31.158:31111/docker/transfer/0", HttpMethod.POST, request ->
        {
            FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
//            formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);
            formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_JSON);
            formHttpMessageConverter.addPartConverter(new MappingJackson2HttpMessageConverter());
            formHttpMessageConverter.write(map, MediaType.MULTIPART_FORM_DATA, request);
        }, new HttpMessageConverterExtractor<>(byte[].class, new ArrayList<>(Collections.singletonList(new ByteArrayHttpMessageConverter()))));
        System.out.println(new File("D:\\env.txt").length() == Objects.requireNonNull(_result).length);
    }

    @Test
    public void transfer1Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/1";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        map.add("id", "1234");
        map.add("name", "4567");
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<byte[]> infos = restTemplate.postForEntity(url, entity, byte[].class);
        System.out.println(new File("D:\\env.txt").length() == Objects.requireNonNull(infos.getBody()).length);
    }

    @Test
    public void sendRequestObject() throws Exception
    {
        String url = "https://192.168.31.158:21111/demo/request/object";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        map.add("id", "1235");
        map.add("name", "4569");

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        Object infos = restTemplate.postForEntity(url,
                entity,
                Object.class);
        System.out.println(infos);


        HttpHeaders _headers = new HttpHeaders();
        _headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> _map = new LinkedMultiValueMap<>();
        _map.add("id", "1236");
        _map.add("name", "4560");
        HttpEntity<MultiValueMap<String, Object>> _entity = new HttpEntity<>(_map, _headers);
        infos = restTemplate.postForEntity(url,
                _entity,
                Object.class);
        System.out.println(infos);
    }

    @Test
    public void transfer2Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/2";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));

        Map<String, String> indexModel = new HashMap<>();
        indexModel.put("id", "abc");
        indexModel.put("name", "eff");
        map.add("indexModel", indexModel);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<byte[]> infos = restTemplate.postForEntity(url, entity, byte[].class);
        System.out.println(new File("D:\\env.txt").length() == Objects.requireNonNull(infos.getBody()).length);
    }

    @Test
    public void transfer3Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/3";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<byte[]> infos = restTemplate.postForEntity(url, entity, byte[].class);
        System.out.println(new File("D:\\env.txt").length() == Objects.requireNonNull(infos.getBody()).length);
    }

    @Test
    public void uploadBytes() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/upload/multiple/bytes";
        byte[] content = "abc123.中文".getBytes(StandardCharsets.UTF_8);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(content, httpHeaders);

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(url,
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<Map<String, String>>()
                {
                });
        System.out.println(response.getBody());
    }
}
