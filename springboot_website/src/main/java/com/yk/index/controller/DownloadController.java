package com.yk.index.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DownloadController
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/16 12:07:14
 */
@Controller
@RequestMapping("/index/download")
public class DownloadController
{
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
     * 不要这么写
     * contentType: "application/json", data: '{"key":"val"}' 或者 data: {"key":"val"} 或者 data: 'key=val'
     * contentType: "application/x-www-form-urlencoded", data: 'key=val' 或者 data: '{"key":"val"}'
     * params都获取不到数据
     */
    @RequestMapping(value = "/v1", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<Map<String, String>> v1(@RequestParam Map<String, String> params) throws IOException
    {
        return ResponseEntity.ok(params);
    }

    /**
     * contentType: "application/json",
     * data: '{"key":"val"}' 或者 data: {"key":"val"}
     * params获取不到数据
     *
     * contentType: "application/x-www-form-urlencoded",
     * data: 'key=val',
     * params正常获取数据
     */
    @RequestMapping(value = "/v2", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> v2(@RequestParam Map<String, String> params) throws IOException
    {
        return ResponseEntity.ok(params);
    }

    /**
     * contentType: "application/json",
     * data: {"key":"val"} -- JSON对象 不能是字符串
     * 正常获取数据
     */
    @RequestMapping(value = "/v3", method = RequestMethod.GET, consumes = "application/json")
    public ResponseEntity<Map<String, String>> v3(@RequestParam Map<String, String> params) throws IOException
    {
        return ResponseEntity.ok(params);
    }

    /**
     * contentType: "application/x-www-form-urlencoded",
     * data: 'key=val'
     * 正常获取数据
     *
     * contentType: "application/x-www-form-urlencoded",
     * data: {"key":"val"} -- JSON对象
     * 正常获取数据
     *
     * contentType: "application/json",
     * data: {"key":"val"} -- JSON对象
     * 正常获取数据
     *
     * contentType: "application/json",
     * data: 'key=val'
     * 正常获取数据
     */
    @RequestMapping(value = "/v4", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> v4(HttpServletRequest request, @RequestParam Map<String, String> params) throws IOException, InterruptedException
    {
//        TimeUnit.SECONDS.sleep(20);
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        return ResponseEntity.ok(params);
    }
}
