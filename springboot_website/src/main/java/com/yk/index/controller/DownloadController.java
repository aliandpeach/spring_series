package com.yk.index.controller;

import com.yk.index.model.IndexModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
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
}
