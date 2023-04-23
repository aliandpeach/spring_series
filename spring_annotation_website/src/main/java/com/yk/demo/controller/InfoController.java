package com.yk.demo.controller;

import com.yk.demo.model.CommonParam;
import com.yk.demo.model.CommonResult;
import com.yk.demo.model.DownloadInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PageController
 */
@RestController
@RequestMapping("/rest")
public class InfoController
{
    @Autowired
    private RestTemplate restTemplate;

    /**
     * {"ids":["1", "2"]} 默认是POST
     */
    @RequestMapping("/of")
    @ResponseBody
    public CommonResult<Map<String, Object>> of(@RequestBody CommonParam<String> body)
    {
        List<String> list = body.getIds();

        return CommonResult.buildSuccess(new HashMap<>(Collections.singletonMap("key1", "val1")));
    }

    /**
     * 后台为什么使用@RequestParam解析？
     * <p>
     * ajax如果不指定上传数据类型Content-Type，默认的是application/x-www-form-urlencoded，这种编码格式后台需要通过RequestParam来处理。
     * <p>
     * 后台为什么参数名称是ids[] 因为前端传值，就是ids[]
     * <p>
     * https://blog.csdn.net/u013737132/article/details/106325903/
     * <p>
             $.ajax({
                  type: "get",
                  contentType: "application/x-www-form-urlencoded",
                  url: "/rest/array",
                  data: 'ids[]=' + 1 + '&ids[]=' + 2 + '&token=t',
                  async: true,
                  success: function (result) {
                  }
              });
     */
    @RequestMapping("/array")
    @ResponseBody
    public CommonResult<Map<String, Object>> array(@RequestParam(value = "ids[]") List<Long> ids)
    {
        return CommonResult.buildSuccess(new HashMap<>(Collections.singletonMap("key1", ids)));
    }

    /**
     *         $.ajax({
     *             type: "get",
     *             contentType: "application/x-www-form-urlencoded",
     *             url: "/rest/string",
     *             data: 'str=' + 11111 + '&token=t',
     *             async: true,
     *             success: function (result) {
     *                 var _range = result.range;
     *                 $("#_calc_result").html(_range);
     *             }
     *         });
     */
    @RequestMapping("/string")
    @ResponseBody
    public CommonResult<Map<String, Object>> string(@RequestParam(value = "str") String str)
    {
        return CommonResult.buildSuccess(new HashMap<>(Collections.singletonMap("str", str)));
    }

    /**
     * 目前 @RequestParam(value = "params") 不能这么写，暂不知怎么把前端传来的 params 字符串直接通过Spring转成Map
     *
     *         $.ajax({
     *             type: "get",
     *             contentType: "application/x-www-form-urlencoded",
     *             url: "/rest/map",
     *             data: 'key1=value1&key2=value2&token=t',
     *             async: true,
     *             success: function (result) {
     *                 var _range = result.range;
     *                 $("#_calc_result").html(_range);
     *             }
     *         });
     */
    @RequestMapping("/map")
    @ResponseBody
    public CommonResult<Map<String, Object>> map(@RequestParam Map<String, String> params)
    {
        return CommonResult.buildSuccess(new HashMap<>(params));
    }

    @RequestMapping("/download")
    public CommonResult<Map<String, Object>> download(@RequestBody @Validated DownloadInfo downloadInfo)
    {
        // 下载文件
        HttpMethod method = HttpMethod.POST.name().equalsIgnoreCase(downloadInfo.getMethod()) ? HttpMethod.POST : HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> entity1 = restTemplate.exchange(downloadInfo.getUrl(), method, entity, byte[].class);
        HttpHeaders httpHeaders = entity1.getHeaders();
        byte[] bytes1 = entity1.getBody();
        String str1 = new String(bytes1, StandardCharsets.UTF_8);

        byte[] bytes2 = restTemplate.execute(downloadInfo.getUrl(), method, req ->
        {

        }, new HttpMessageConverterExtractor<byte[]>(byte[].class, Objects.requireNonNull(restTemplate.getMessageConverters())));
        String str2 = new String(bytes1, StandardCharsets.UTF_8);

        byte[] bytes3 = restTemplate.execute(downloadInfo.getUrl(), method, req ->
        {

        }, new HttpMessageConverterExtractor<byte[]>(byte[].class, new ArrayList<>(Arrays.asList(new ByteArrayHttpMessageConverter()))));
        String str3 = new String(bytes1, StandardCharsets.UTF_8);

        restTemplate.execute(downloadInfo.getUrl(), method, req ->
        {

        }, new HttpMessageConverterExtractor<byte[]>(byte[].class, new ArrayList<>(Arrays.asList(new ByteArrayHttpMessageConverter()
        {
            @Override
            public byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage inputMessage) throws IOException
            {
                HttpHeaders _httpHeaders = inputMessage.getHeaders();
                ContentDisposition contentDisposition = _httpHeaders.getContentDisposition();
                OutputStream out = new FileOutputStream(System.getProperty("user.dir") + File.separator + contentDisposition.getFilename());
                int len;
                byte[] buf = new byte[8192];
                InputStream input = inputMessage.getBody();
                while ((len = input.read(buf)) != -1)
                {
                    out.write(buf, 0, len);
                }
                return new byte[0];
            }
        }))));
        return CommonResult.buildSuccess(new HashMap<>());
    }
}