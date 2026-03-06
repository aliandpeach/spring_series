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
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RestTemplateTest
{
    private RestTemplate restTemplate;

    @Before
    public void before()
    {
        HttpComponentsClientHttpRequestFactory httpRequestFactory;
        try
        {
            HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 7081, "http");
            // 使用 http-client组件
            httpRequestFactory = new HttpComponentsClientHttpRequestFactory(new HttpClientUtil(new HttpClientUtil.Config().ofProxy(proxyInfo)).httpClient);
//            httpRequestFactory = new HttpComponentsClientHttpRequestFactory(new HttpClientUtil(new HttpClientUtil.Config()).httpClient);
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

        // FormHttpMessageConverter可读取解析并发送HttpEntity<MultiValueMap<String, Object>>的内容参数为form-data/x-www-form-urlencoded报文格式
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        // 下面一行配置, FormHttpMessageConverter就可解析MultiValueMap, 使FormHttpMessageConverter支持application/octet-stream格式;
        // 在 RestTemplate$HttpEntityRequestCallback.doWithRequest, 就可以正常读取解析并发送文件流类型(目前看来有没有这个配置都能完成文件上传)
//        formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);

        // 下面两行的配置, FormHttpMessageConverter就可以解析对象参数, 使其转为json格式(主要为了支持form-data中的复杂对象, 例如请求同时存在@RequestPart file和@RequestPart MyObj)
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
        // 接口"/transfer/0"返回的是ResponseEntity<byte[]>
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


        map = new LinkedMultiValueMap<>();
        map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        indexModel = new HashMap<>();
        indexModel.put("id", "abc");
        indexModel.put("name", "eff");
        map.add("indexModel", indexModel);
        entity = new HttpEntity<>(map, headers);
        ResponseEntity<byte[]> result2 = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);


        MultiValueMap<String, Object>_map = new LinkedMultiValueMap<>();
        _map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        indexModel.put("id", "567");
        indexModel.put("name", "789");
        _map.add("indexModel", indexModel);
        byte[] _result = restTemplate.execute(url, HttpMethod.POST,
                request ->
                {
                    FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
//                  formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);
                    formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_JSON);
                    formHttpMessageConverter.addPartConverter(new MappingJackson2HttpMessageConverter());
                    formHttpMessageConverter.write(_map, MediaType.MULTIPART_FORM_DATA, request);
                },
                new HttpMessageConverterExtractor<>(byte[].class, new ArrayList<>(Collections.singletonList(new ByteArrayHttpMessageConverter()))));
        System.out.println(new File("D:\\env.txt").length() == Objects.requireNonNull(_result).length);
    }

    @Test
    public void transfer1Test() throws Exception
    {
        // 接口"/transfer/1"返回的是ResponseEntity<byte[]>, 参数IndexModel 是普通参数, 没有@RequestPart修饰
        String url = "https://192.168.31.158:31111/docker/transfer/1";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        map.add("id", "1234");
        map.add("name", "4567");
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<byte[]> infos = restTemplate.postForEntity(url, entity, byte[].class);

        MultiValueMap<String, Object> _map = new LinkedMultiValueMap<>();
        _map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        _map.add("id", "1234");
        _map.add("name", "4567");
        HttpEntity<MultiValueMap<String, Object>> _entity = new HttpEntity<>(_map, headers);

        ResponseEntity<byte[]> _infos = restTemplate.exchange(url, HttpMethod.POST, _entity, byte[].class);
        System.out.println(new File("D:\\env.txt").length() == Objects.requireNonNull(_infos.getBody()).length);

        MultiValueMap<String, Object> __map = new LinkedMultiValueMap<>();
        __map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        __map.add("id", "567");
        __map.add("name", "789");
        byte[] _result = restTemplate.execute(url, HttpMethod.POST,
                request ->
                {
                    FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
//                  formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);
                    formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_JSON);
                    formHttpMessageConverter.addPartConverter(new MappingJackson2HttpMessageConverter());
                    formHttpMessageConverter.write(__map, MediaType.MULTIPART_FORM_DATA, request);
                },
                new HttpMessageConverterExtractor<>(byte[].class, new ArrayList<>(Collections.singletonList(new ByteArrayHttpMessageConverter()))));
        System.out.println(new File("D:\\env.txt").length() == Objects.requireNonNull(_result).length);
    }

    @Test
    public void transfer2Test() throws Exception
    {
        // 接口"/transfer/2"返回的是byte[], 返回类型必须指定二进制: produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, 否则postForEntity/exchange二者都会因为找不到正确解析器类而解析结果失败
        // 但是如果没有指定produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, 使用 restTemplate.execute()自定义响应解析HttpMessageConverterExtractor类也可以正常返回
        // 这是因为返回的接口返回的类型被指定为了application/json类型, 无法找到解析器(HttpMessageConverterExtractor.extractData), 自定义响应解析HttpMessageConverterExtractor类不存在找不到的问题
        String url = "https://192.168.31.158:31111/docker/transfer/2";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> _map = new LinkedMultiValueMap<>();
        _map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        Map<String, String> _indexModel = new HashMap<>();
        _indexModel.put("id", "abc");
        _indexModel.put("name", "eff");
        _map.add("indexModel", _indexModel);
        byte[] _result = restTemplate.execute(url, HttpMethod.POST,
                request ->
                {
                    FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
//                  formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);
                    formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_JSON);
                    formHttpMessageConverter.addPartConverter(new MappingJackson2HttpMessageConverter());
                    formHttpMessageConverter.write(_map, MediaType.MULTIPART_FORM_DATA, request);
                },
                new HttpMessageConverterExtractor<>(byte[].class, new ArrayList<>(Collections.singletonList(new ByteArrayHttpMessageConverter()))));
        System.out.println(new File("D:\\env.txt").length() == Objects.requireNonNull(_result).length);


        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));

        Map<String, String> indexModel = new HashMap<>();
        indexModel.put("id", "abc");
        indexModel.put("name", "eff");
        map.add("indexModel", indexModel);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<byte[]> infos = restTemplate.postForEntity(url, entity, byte[].class);
        System.out.println(new File("D:\\env.txt").length() == Objects.requireNonNull(infos.getBody()).length);

        map = new LinkedMultiValueMap<>();
        map.add("file", new MultipartInputStreamFileResource(new FileInputStream("D:\\env.txt"), "env.txt"));
        indexModel = new HashMap<>();
        indexModel.put("id", "abc");
        indexModel.put("name", "eff");
        map.add("indexModel", indexModel);
        entity = new HttpEntity<>(map, headers);
        ResponseEntity<byte[]> result2 = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);
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
    public void transfer4Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/4";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("id", "1235");
        map.add("name", "4569");
        // 使用对象传入indexList后台实际的值不正确
        // map.add("indexList", Arrays.stream(new String[]{"12", "23"}).collect(Collectors.toList()));
        map.add("indexList", "12,23");
        map.add("active", true);

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
        // 使用对象传入indexList后台实际的值不正确
        // _map.add("indexList", Arrays.stream(new String[]{"13", "25"}).collect(Collectors.toList()));
        _map.add("indexList", "13,25");
        HttpEntity<MultiValueMap<String, Object>> _entity = new HttpEntity<>(_map, _headers);
        Object _infos = restTemplate.postForEntity(url,
                _entity,
                Object.class);
        System.out.println(_infos);
    }

    @Test
    public void transfer5Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/5";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        Map<String, Object> indexModel = new HashMap<>();
        indexModel.put("id", "1235");
        indexModel.put("name", "4569");
        indexModel.put("indexList", Arrays.stream(new String[]{"112", "223"}).collect(Collectors.toList()));
        map.add("indexModel", indexModel);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        Object infos = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
        System.out.println(infos);
    }

    @Test
    public void transfer6Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/6";

        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> indexModel = new HashMap<>();
        indexModel.put("id", "1235");
        indexModel.put("name", "4569");
        indexModel.put("indexList", Arrays.stream(new String[]{"122", "233"}).collect(Collectors.toList()));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(indexModel, headers);
        Object infos = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
        System.out.println(infos);
    }

    @Test
    public void transfer7Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/7";

        HttpHeaders headers = new HttpHeaders();
        List<String> idList = new ArrayList<>();
        idList.add("1221");
        idList.add("2331");

        HttpEntity<List<String>> entity = new HttpEntity<>(idList, headers);
        Object infos = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
        System.out.println(infos);
    }

    @Test
    public void transfer8Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/8";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("idList", "12,13");

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        Object infos = restTemplate.postForEntity(url,
                entity,
                Object.class);
        System.out.println(infos);


        HttpHeaders _headers = new HttpHeaders();
        _headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> _map = new LinkedMultiValueMap<>();
        _map.add("idList", "12,13");
        HttpEntity<MultiValueMap<String, Object>> _entity = new HttpEntity<>(_map, _headers);
        Object _infos = restTemplate.postForEntity(url,
                _entity,
                Object.class);
        System.out.println(_infos);

        url = "https://192.168.31.158:31111/docker/transfer/8x";

        headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        map = new LinkedMultiValueMap<>();
        map.add("idList", "1234");
        map.add("idList", "1236");

        entity = new HttpEntity<>(map, headers);
        infos = restTemplate.postForEntity(url, entity, Object.class);
        System.out.println(infos);


        _headers = new HttpHeaders();
        _headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        _map = new LinkedMultiValueMap<>();
        _map.add("idList", "1234");
        _map.add("idList", "1235");
        _entity = new HttpEntity<>(_map, _headers);
        _infos = restTemplate.postForEntity(url, _entity, Object.class);
        System.out.println(_infos);
    }

    @Test
    public void transfer9Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/9";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        List<String> idList = new ArrayList<>();
        idList.add("12");
        idList.add("13");
        map.add("idList", idList);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        Object infos = restTemplate.postForEntity(url,
                entity,
                Object.class);
        System.out.println(infos);
    }

    @Test
    public void transfer10Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/10";

        HttpHeaders headers = new HttpHeaders();
        Map<String, String> map = new HashMap<>();
        map.put("1", "1221");
        map.put("2", "2331");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);
        Object infos = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
        System.out.println(infos);
    }

    @Test
    public void transfer11Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/11";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("1", "113");
        map.add("2", "113");

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        Object infos = restTemplate.postForEntity(url,
                entity,
                Object.class);
        System.out.println(infos);


        url = "https://192.168.31.158:31111/docker/transfer/11";
        HttpHeaders _headers = new HttpHeaders();
        _headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> _map = new LinkedMultiValueMap<>();
        _map.add("1", "12");
        _map.add("3", "4");
        HttpEntity<MultiValueMap<String, Object>> _entity = new HttpEntity<>(_map, _headers);
        Object _infos = restTemplate.postForEntity(url,
                _entity,
                Object.class);
        System.out.println(_infos);
    }

    @Test
    public void transfer12Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/12";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> _map = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("1", "12");
        map.put("2", "13");
        _map.add("map", map);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(_map, headers);
        Object infos = restTemplate.postForEntity(url,
                entity,
                Object.class);
        System.out.println(infos);
    }

    @Test
    public void transfer13Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/13";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("name", "113");
        map.add("age", 22);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        String infos = restTemplate.execute(url, HttpMethod.POST, new RequestCallback()
        {
            @Override
            public void doWithRequest(ClientHttpRequest request) throws IOException
            {
                FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
//                  formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);
                formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_JSON);
                formHttpMessageConverter.addPartConverter(new MappingJackson2HttpMessageConverter());
                formHttpMessageConverter.write(map, MediaType.MULTIPART_FORM_DATA, request);
            }
        }, new HttpMessageConverterExtractor<>(String.class, Collections.singletonList(new StringHttpMessageConverter())));
        System.out.println(infos);

        HttpHeaders _headers = new HttpHeaders();
        _headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> _map = new LinkedMultiValueMap<>();
        _map.add("name", "113");
        _map.add("age", 23);
        HttpEntity<MultiValueMap<String, Object>> _entity = new HttpEntity<>(_map, _headers);
        String _infos = restTemplate.execute(url, HttpMethod.POST, new RequestCallback()
        {
            @Override
            public void doWithRequest(ClientHttpRequest request) throws IOException
            {
                FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
//                  formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);
                formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_JSON);
                formHttpMessageConverter.addPartConverter(new MappingJackson2HttpMessageConverter());
                formHttpMessageConverter.write(_map, MediaType.APPLICATION_FORM_URLENCODED, request);
            }
        }, new HttpMessageConverterExtractor<>(String.class, Collections.singletonList(new StringHttpMessageConverter())));
        System.out.println(_infos);
    }

    @Test
    public void transfer14Test() throws Exception
    {
        String url = "https://192.168.31.158:31111/docker/transfer/14";

        /*HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        Map<String, String> m = new HashMap<>();
        m.put("name", "113");
        m.put("id", "id1");
        map.add("indexModel", m);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        Object infos = restTemplate.postForEntity(url, entity, Object.class);
        System.out.println(infos);*/

        HttpHeaders _headers = new HttpHeaders();
        _headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> _map = new LinkedMultiValueMap<>();
        Map<String, String> _m = new HashMap<>();
        _m.put("name", "113");
        _m.put("id", "id1");
        _map.add("indexModel", _m);
        HttpEntity<MultiValueMap<String, Object>> _entity = new HttpEntity<>(_map, _headers);
        Object _infos = restTemplate.postForEntity(url, _entity, Object.class);
        System.out.println(_infos);
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
