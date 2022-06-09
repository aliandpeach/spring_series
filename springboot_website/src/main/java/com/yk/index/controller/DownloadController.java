package com.yk.index.controller;

import com.yk.index.model.IndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * DownloadController
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/16 12:07:14
 */
@RestController
@RequestMapping("/index/download")
public class DownloadController
{
    private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(HttpServletResponse response, MultipartHttpServletRequest request) throws IOException
    {
        MultiValueMap<String, MultipartFile> multipartFileMultiValueMap = request.getMultiFileMap();
        Map<String, MultipartFile> multipartFileMap = request.getFileMap();
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    /**
     * $.ajax({
     *   url: "/index/download/file",
     *   type: "POST",
     *   contentType: "application/x-www-form-urlencoded",
     *   data: 'downloadName=aaa.zip',
     *   async: true,
     *   success: function (res) {
     *   }
     * });
     * common.js:
     * download("/index/download/file", {"downloadName":"aaa.zip"})
     * ============================================================
     * application/x-www-form-urlencoded 类型请求, controller参数的写法:
     * 1. @RequestParam("downloadName") String downloadName
     * 2. @RequestParam Map<String, String> params
     * 3. UserInfo params
     * ajax中 data属性 格式为 'key=val&key1=val1'
     * ============================================================
     * consumes = "application/json"的情况下 不要使用 @RequestParam, 目前无法转换ajax发送的json数据为对象
     * application/json 就正常使用@RequestBody就可以了
     */
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public void downloadFile(HttpServletResponse response, @RequestParam("downloadName") String downloadName) throws IOException
    {
        downloadName = "bmj-new-install-1.5.0.0.7.v5.zip";
        try (InputStream input = new FileInputStream("F:\\bmj-new-install-1.5.0.0.7.v5.zip");
             BufferedOutputStream bufferedOut = new BufferedOutputStream(response.getOutputStream()))
        {
            String filename = new String((System.currentTimeMillis() + downloadName).getBytes(), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentType("application/octet-stream");
            int len;
            byte[] buf = new byte[8192];
            while ((len = input.read(buf)) != -1)
            {
                bufferedOut.write(buf, 0, len);
            }
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    /**
     * POST接口使用@RequestParam接收参数，在前端使用application/x-www-form-urlencoded传递参数，最后参数都会放入form-data
     *
       contentType: "application/x-www-form-urlencoded" 可以将key=val格式的数据, 放入form-data
       $.ajax({
         type: "post",
         contentType: "application/x-www-form-urlencoded",
         url: "/index/download/v2",
         data: 'key=val',
         async: true,
         success: function (result) {
             // debugger
         }
        });

       contentType: "application/x-www-form-urlencoded" 可以将data解析为key=val格式, 放入form-data
       $.ajax({
         type: "post",
         contentType: "application/x-www-form-urlencoded",
         url: "/index/download/v2",
         data: {"key":"val"},
         async: true,
         success: function (result) {
           // debugger
         }
       });
     *
     * 上面两个AJAX如果contentType换成 application/json
     * contentType: "application/json",
     * data: 'key=val' 或者 data: {"key":"val"} 格式的数据都能正常执行，但是后台接收不到参数值
     * 此时后台应该使用@RequestBody, 且data格式也只能是'{"key":"val"}'字符串
     */
    @RequestMapping(value = "/v2", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> v2(@RequestParam Map<String, String> params) throws IOException
    {
        return ResponseEntity.ok(params);
    }

    /**
     * GET接口使用@RequestParam接收参数，在前端无论使用application/x-www-form-urlencoded或者 application/json传递参数，最后参数都会追加到url后面
     *
     *  $.ajax({
            type: "get",
            contentType: "application/json",
            url: "/index/download/v4",
            data: 'key=val',
             async: true,
             success: function (result) {
                 // debugger
             }
         });
         $.ajax({
             type: "get",
             contentType: "application/json",
             url: "/index/download/v4",
             data: {"key":"val"},
             async: true,
             success: function (result) {
             // debugger
             }
         });
         $.ajax({
             type: "get",
             contentType: "application/x-www-form-urlencoded",
             url: "/index/download/v4",
             data: 'key=val',
             async: true,
             success: function (result) {
             // debugger
             }
         });
         $.ajax({
             type: "get",
             contentType: "application/x-www-form-urlencoded",
             url: "/index/download/v4",
             data: {"key":"val"},
             async: true,
             success: function (result) {
             // debugger
             }
         });
     *
     * contentType: "application/json",
     * data: '{"key":"val"}' JSON字符串
     * 不能正常获取数据, json字符串的格式只用于@ResponseBody接收参数
     */
    @RequestMapping(value = "/v4", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> v4(HttpServletRequest request, @RequestParam Map<String, String> params) throws IOException, InterruptedException
    {
//        TimeUnit.SECONDS.sleep(20);
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        return ResponseEntity.ok(params);
    }

    /**
         $.ajax({
             type: "post",
             contentType: "application/x-www-form-urlencoded",
             url: "/index/download/v5",
             data: {"name":"name1"},
             async: true,
             success: function (result) {
             // debugger
             }
         });
         $.ajax({
             type: "post",
             contentType: "application/x-www-form-urlencoded",
             url: "/index/download/v5",
             data: 'name=name1',
             async: true,
             success: function (result) {
             // debugger
             }
         });
     */
    @RequestMapping(value = "/v5", method = RequestMethod.POST)
    public ResponseEntity<IndexModel> v5(IndexModel indexModel)
    {
        return ResponseEntity.ok(indexModel);
    }

    /**
         $.ajax({
             type: "get",
             contentType: "application/x-www-form-urlencoded",
             url: "/index/download/v4",
             data: {"name":"name1"},
             async: true,
             success: function (result) {
             // debugger
             }
         });
         $.ajax({
             type: "get",
             contentType: "application/x-www-form-urlencoded",
             url: "/index/download/v4",
             data: 'name=name1',
             async: true,
             success: function (result) {
             // debugger
             }
         });

         $.ajax({
             type: "get",
             contentType: "application/json",
             url: "/index/download/v4",
             data: {"name":"name1"},
             async: true,
             success: function (result) {
             // debugger
             }
         });
         $.ajax({
             type: "get",
             contentType: "application/json",
             url: "/index/download/v4",
             data: 'name=name1',
             async: true,
             success: function (result) {
             // debugger
             }
         });
     */
    @RequestMapping(value = "/v6", method = RequestMethod.GET)
    public ResponseEntity<IndexModel> v6(IndexModel indexModel)
    {
        return ResponseEntity.ok(indexModel);
    }

    /**
     * 使用form-data传值，参数内容为json格式，ContentType=application/json
     *
     * @param indexModel indexModel
     * @return ResponseEntity
     */
    @RequestMapping(value = "/v7", method = RequestMethod.POST)
    public ResponseEntity<IndexModel> v7(@RequestPart("indexModel") IndexModel indexModel)
    {
        return ResponseEntity.ok(indexModel);
    }

    @RequestMapping(value = "/v8", method = RequestMethod.POST)
    public ResponseEntity<String> download(@RequestPart("file") MultipartFile file,
                                           @RequestPart("indexModel") IndexModel indexModel)
    {
        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("Connection", "keep-alive");
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            map.add("indexModel", new IndexModel("name-1", "id-1", true));
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);

            // 1.1 目标接口需要设置响应头为 Content-Type=application/octet-stream
            ResponseEntity<byte[]> result = restTemplate.exchange("https://192.168.31.105:31111/docker/transfer", HttpMethod.POST, entity, byte[].class);

            map.remove("file");
            map.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            map.remove("indexModel");
            map.add("indexModel", new IndexModel("name-1", "id-1", false));
            // 1.2 该方式直接指定ByteArrayHttpMessageConverter 不需要目标接口设置响应头
            byte[] _result = restTemplate.execute("https://192.168.31.105:31111/docker/transfer", HttpMethod.POST, request ->
            {
                FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
                formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);
                formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_JSON);
                formHttpMessageConverter.addPartConverter(new MappingJackson2HttpMessageConverter());
                formHttpMessageConverter.write(map, MediaType.MULTIPART_FORM_DATA, request);
            }, new HttpMessageConverterExtractor<>(byte[].class, new ArrayList<>(Collections.singletonList(new ByteArrayHttpMessageConverter()))));


            map.remove("file");
            map.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            map.remove("indexModel");
            map.add("indexModel", new IndexModel("name-1", "id-1", true));
            // 2.1 Controller 直接返回byte[] 无法通过response.setHeader设置响应头为 Content-Type=application/octet-stream
            ResponseEntity<byte[]> result2 = restTemplate.exchange("https://192.168.31.105:31111/docker/transfer2", HttpMethod.POST, entity, byte[].class);


            map.remove("file");
            map.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            map.remove("indexModel");
            map.add("indexModel", new IndexModel("name-1", "id-1", false));
            // 2.2
            byte[] _result2 = restTemplate.execute("https://192.168.31.105:31111/docker/transfer2", HttpMethod.POST, request ->
            {
                FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
                formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_OCTET_STREAM);
                formHttpMessageConverter.addSupportedMediaTypes(MediaType.APPLICATION_JSON);
                formHttpMessageConverter.addPartConverter(new MappingJackson2HttpMessageConverter());
                formHttpMessageConverter.write(map, MediaType.MULTIPART_FORM_DATA, request);
            }, new HttpMessageConverterExtractor<>(byte[].class, new ArrayList<>(Collections.singletonList(new ByteArrayHttpMessageConverter()))));

            map.remove("file");
            map.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            map.remove("indexModel");
            map.add("indexModel", new IndexModel("name-1", "id-1", false));
            // 2.3
            ResponseEntity<byte[]> __result2 = restTemplate.postForEntity("https://192.168.31.105:31111/docker/transfer2", entity, byte[].class);

            map.remove("file");
            map.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            // 3.1
            ResponseEntity<byte[]> result3 = restTemplate.postForEntity("https://192.168.31.105:31111/docker/transfer3", entity, byte[].class);
            logger.info("");
        }
        catch (Exception e)
        {
            logger.error("rest template execute error {}", e.getMessage());
        }
        return ResponseEntity.ok("OK");
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
}
