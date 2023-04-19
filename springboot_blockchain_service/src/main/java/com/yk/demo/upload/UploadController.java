package com.yk.demo.upload;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UploadController
 *
 * @author yangk
 * @version 1.0
 * @since 2021/4/27 18:05
 */

@RestController
@RequestMapping("/import")
public class UploadController
{
    private static final Logger _logger = LoggerFactory.getLogger(UploadController.class);

    /**
     * from 提交的内容必须包含名称 fileName_1
     * <p>
     * 如果这个名称中提交了多个文件 则需要使用 request.getFiles("fileName_1"); 或者参数定义为 @RequestPart("fileName_1") MultipartFile[]
     *
     * 如果文件的提交使用了多个input, 每个input只有单个文件, 即 fileName_1、fileName_2 则使用request.getFileMap(); （当然也可以定义多个 @RequestPart("fileName_xxx") MultipartFile[]）
     *
     * 如果文件的提交使用了多个input, 每个input多个文件, 即 fileName_1、fileName_2 则使用request.getMultiFileMap();
     *
     * @param files1
     * @param files2
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/upload/multiple/files")
    @ResponseBody
    public Map<String, List<String>> multipleUploadFiles(@RequestPart(required = false, name = "fileName_1") MultipartFile[] files1,
                                                         @RequestPart(required = false, name = "fileName_2") MultipartFile[] files2,
                                                         MultipartHttpServletRequest request) throws
            IOException
    {
        // List<MultipartFile> files = request.getFiles("fileName_1");
        // List<MultipartFile> files2 = request.getFiles("fileName_2");
        // Map<String, MultipartFile> maps = request.getFileMap(); // 每个 name 下是单个文件的时候 用这个
        MultiValueMap<String, MultipartFile> multiValueMaps = request.getMultiFileMap(); // 每个 name 下是多个文件的时候 用这个
        Map<String, List<String>> result = new HashMap<>();
        result.putIfAbsent("failed", new ArrayList<>());
        result.putIfAbsent("success", new ArrayList<>());
        String root = System.getProperty("user.dir");
        multiValueMaps.forEach((key, value) -> value.forEach(f ->
        {
            try
            {
                f.transferTo(new File(root + File.separator + f.getOriginalFilename()));
                result.get("success").add(f.getOriginalFilename());
            }
            catch (IOException e)
            {
                result.get("failed").add(f.getOriginalFilename());
            }
        }));
        return result;
    }
    
    @PostMapping("/upload/multipart/http/request")
    @ResponseBody
    public String uploadMultipartHttpRequest(MultipartHttpServletRequest request) throws IOException
    {
        String root = System.getProperty("user.dir");
        MultipartFile uploadFile = request.getFile("_upload_single_one");
        // 若是_upload_single_one下传入的文件数量大于1, 使用下面的方法
        // List<MultipartFile> uploadFiles = request.getFiles("_upload_single_one");
        long cur = System.currentTimeMillis();
        uploadFile.transferTo(new File(root + File.separator + cur + "_" + uploadFile.getOriginalFilename()));
        return "SUCCESS:" + cur + "_" + uploadFile.getOriginalFilename();
    }
    
    @PostMapping("/upload/multipart/file")
    @ResponseBody
    public String uploadMultipartFile(@RequestPart("_upload_single_two") MultipartFile uploadFile) throws
            IOException
    {
        String root = System.getProperty("user.dir");
        long cur = System.currentTimeMillis();
        uploadFile.transferTo(new File(root + File.separator + cur + "_" + uploadFile.getOriginalFilename()));
        return "SUCCESS:" + cur + "_" + uploadFile.getOriginalFilename();
    }
    
    /**
     * 参数中同时包含了文件和application/json格式的数据
     *
     * 使用postman调用, 或者jmeter 服务端SSL双向认证时, 如果使用Burp Suite抓包,则postman或者jmeter和Burp Suite都需要配置私钥证书(pkcs12即可)
     *
     * 指定标准的http 响应码：
     * 1. 使用ResponseEntity   ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>());
     * 2. HttpServletResponse.setStatus(201)
     * 3. 方法上加入注解：
     *    a. @ExceptionHandler(value = ArrayIndexOutOfBoundsException.class)
     *    b. @ResponseStatus(HttpStatus.BAD_REQUEST)
     * 4. 全局统一处理 ControllerExceptionHandler
     */
    @PostMapping("/upload/multiple/request/part/params")
    @ResponseBody
    public Map<String, Object> multipleUploadParams(MultipartHttpServletRequest request,
                                                    @RequestPart(required = false, name = "params") Map<String, String> params,
                                                    HttpServletResponse response)
    {
        Map<String, Object> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        List<String> r = new ArrayList<>();
        String root = System.getProperty("user.dir");
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File(root + File.separator + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                r.add(t.getValue().getOriginalFilename());
                result.put("success", r);
            }
            catch (IOException e)
            {
                r.add(t.getValue().getOriginalFilename());
                result.put("failed", r);
            }
        });
        result.put("params", params);
        return result;
    }

    /**
     * 可以使用postman调用, 需要分别传入 item中的name和value属性
     *
     * POST /upload/multiple/request/params HTTP/1.1
     * User-Agent: PostmanRuntime/7.26.8
     * Host: 192.190.10.122:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: multipart/form-data; boundary=--------------------------570002456370464510172609
     * Content-Length: 614
     *
     * ----------------------------570002456370464510172609
     * Content-Disposition: form-data; name="1"; filename="test_secret17.txt"
     * Content-Type: text/plain
     *
     * -文件字节参数-
     * Content-Disposition: form-data; name="_key" // _key是params中的key, 111是对应的值
     *
     * 111
     * ----------------------------570002456370464510172609
     * Content-Disposition: form-data; name="_value" // _value是params中的key, 222是对应的值
     *
     * 222
     * ----------------------------570002456370464510172609--
     */
    @PostMapping("/upload/multiple/request/params")
    @ResponseBody
    public Map<String, Object> multipleUploadRequestParam(MultipartHttpServletRequest request, @RequestParam Map<String, String> params)
    {
        String value = params.get("params");
        Map<String, Object> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        List<String> r = new ArrayList<>();
        String root = System.getProperty("user.dir");
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File(root + File.separator + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                r.add(t.getValue().getOriginalFilename());
                result.put("success", r);
            }
            catch (IOException e)
            {
                r.add(t.getValue().getOriginalFilename());
                result.put("failed", r);
            }
        });
        result.put("params", params);
        return result;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor()
    {
        return new MethodValidationPostProcessor();
    }

    /**
     * 上传文件接口, 附带json格式参数, 可以使用postman调用, 需要传入level
     *
     * 这里的 @NotEmpty不生效不知道怎么回事
     *
     *
     *
     * Host: 192.190.10.122:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: multipart/form-data; boundary=--------------------------153054330803524445015047
     * Content-Length: 514
     *
     * ----------------------------153054330803524445015047
     * Content-Disposition: form-data; name="1"; filename="test_secret17.txt"
     * Content-Type: text/plain
     *
     *
     * -文件字节参数-
     * ----------------------------153054330803524445015047
     * Content-Disposition: form-data; name="level"  // String level = 011111
     *
     * 011111
     * ----------------------------153054330803524445015047--
     */
    @PostMapping("/upload/multiple/request/param/name")
    @ResponseBody
    @Validated
    public Map<String, Object> multipleUploadRequestParamStringName(MultipartHttpServletRequest request,
                                                     @NotEmpty(message = "level is empty") @RequestParam(value = "level", required = false) String level)
    {
        Map<String, Object> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        List<String> r = new ArrayList<>();
        String root = System.getProperty("user.dir");
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File(root + File.separator + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                r.add(t.getValue().getOriginalFilename());
                result.put("success", r);
            }
            catch (IOException e)
            {
                r.add(t.getValue().getOriginalFilename());
                result.put("failed", r);
            }
        });
        result.put("level", level);
        return result;
    }

    /**
     * 可以使用postman调用, 需要分别传入 item中的name和value属性
     *
     * POST /upload/multiple/validated/item HTTP/1.1
     * User-Agent: PostmanRuntime/7.26.8
     * Host: 192.190.10.122:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: multipart/form-data; boundary=--------------------------570002456370464510172609
     * Content-Length: 614
     *
     * ----------------------------570002456370464510172609
     * Content-Disposition: form-data; name="1"; filename="test_secret17.txt"
     * Content-Type: text/plain
     *
     * -文件字节参数-
     * Content-Disposition: form-data; name="name"
     *
     * 111
     * ----------------------------570002456370464510172609
     * Content-Disposition: form-data; name="value"
     *
     * 222
     * ----------------------------570002456370464510172609--
     */
    @PostMapping("/upload/multiple/validated/item")
    @ResponseBody
    public Map<String, Object> multipleUploadValidatedItem(MultipartHttpServletRequest request, @Validated Item item)
    {
        Map<String, Object> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        List<String> r = new ArrayList<>();
        String root = System.getProperty("user.dir");
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File(root + File.separator + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                r.add(t.getValue().getOriginalFilename());
                result.put("success", r);
            }
            catch (IOException e)
            {
                r.add(t.getValue().getOriginalFilename());
                result.put("failed", r);
            }
        });
        result.put("item", item);
        return result;
    }

    //  不要这么写, file和body没法同时传值
    @PostMapping("/upload/multiple/validated/request/body/item")
    @ResponseBody
    public Map<String, Object> multipleUploadValidatedRequestBodyItem(MultipartHttpServletRequest request,  @RequestBody @Validated Item item)
    {
        Map<String, Object> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        List<String> r = new ArrayList<>();
        String root = System.getProperty("user.dir");
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File(root + File.separator + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                r.add(t.getValue().getOriginalFilename());
                result.put("success", r);
            }
            catch (IOException e)
            {
                r.add(t.getValue().getOriginalFilename());
                result.put("failed", r);
            }
        });
        result.put("item-body", item);
        return result;
    }

    /**
     *  使用@RequestPart接收对象 报文中必须指定格式为 Content-Type: application/json
     *
     *  Host: 192.190.10.122:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: multipart/form-data; boundary=--------------------------748011833044869520584701
     * Content-Length: 511
     *
     * ----------------------------748011833044869520584701
     * Content-Disposition: form-data; name="1"; filename="test_secret17.txt"
     * Content-Type: text/plain
     *
     *
     * -文件字节参数-
     * ----------------------------748011833044869520584701
     * Content-Disposition: form-data; name="items"
     * Content-Type: application/json
     *
     * []
     * ----------------------------748011833044869520584701--
     */
    @PostMapping("/upload/multiple/request/part/items")
    @ResponseBody
    public Map<String, Object> uploadMultipleRequestPartItems(MultipartHttpServletRequest request, @RequestPart List<Item> items)
    {
        Map<String, Object> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        List<String> r = new ArrayList<>();
        String root = System.getProperty("user.dir");
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File(root + File.separator + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                r.add(t.getValue().getOriginalFilename());
                result.put("success", r);
            }
            catch (IOException e)
            {
                r.add(t.getValue().getOriginalFilename());
                result.put("failed", r);
            }
        });
        result.put("items", items);
        return result;
    }

    /**
     * 使用@RequestPart接收对象
     *
     * POST /import/upload/multiple/json7 HTTP/1.1
     * User-Agent: PostmanRuntime/7.26.8
     * Host: 192.190.10.122:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: multipart/form-data; boundary=--------------------------038190145852481148403885
     * Content-Length: 511
     *
     * ----------------------------038190145852481148403885
     * Content-Disposition: form-data; name="xxxx"; filename="test.txt"
     * Content-Type: text/plain
     *
     * -文件字节参数-
     * ----------------------------038190145852481148403885
     * Content-Disposition: form-data; name="json"
     *
     * -字符串参数-
     * ----------------------------038190145852481148403885--
     */
    @PostMapping("/upload/multiple/request/part/string/name")
    @ResponseBody
    public Map<String, Object> multipleUploadRequestPartStringName(MultipartHttpServletRequest request, @RequestPart(name = "json") String json)
    {
        Map<String, Object> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        List<String> r = new ArrayList<>();
        String root = System.getProperty("user.dir");
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File(root + File.separator + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                r.add(t.getValue().getOriginalFilename());
                result.put("success", r);
            }
            catch (IOException e)
            {
                r.add(t.getValue().getOriginalFilename());
                result.put("failed", r);
            }
        });
        result.put("json", json);
        return result;
    }

    /**
     *
     */
    @PostMapping(value = "/upload/multiple/bytes")
    @ResponseBody
    public Map<String, String> uploadBytes(@RequestBody byte[] bytes) throws IOException
    {
        String string = new String(bytes, StandardCharsets.ISO_8859_1);
        String string2 = new String(bytes, StandardCharsets.UTF_8);
        try (ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
             BufferedInputStream buffered = new BufferedInputStream(bai);
             InputStreamReader reader = new InputStreamReader(buffered, StandardCharsets.UTF_8);
             StringWriter sw = new StringWriter())
        {
            int len = 0;
            char[] buffer = new char[8192];
            while ((len = reader.read(buffer)) != -1)
            {
                sw.write(buffer, 0, len);
            }
            sw.flush();
            String content = sw.toString();
            System.out.println(content);
        }
        return new HashMap<>(Collections.singletonMap("SUCCESS", "OK"));
    }
    
    @PostMapping("/download")
    @ResponseBody
    public void download(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> params)
    {
        Map<String, String[]> parameters = request.getParameterMap();
        String downloadName = params.get("download.name");
        try (InputStream input = new FileInputStream(System.getProperty("user.dir") + File.separator + downloadName);
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
        
        }
    }
    
    @PostMapping(value = "/download/bytes", produces = "application/octet-stream")
    @ResponseBody
    public byte[] downloadBytes(@RequestBody Map<String, String> params)
    {
        String name = params.get("download.name");
        try (InputStream input = new FileInputStream(System.getProperty("user.dir") + File.separator + name);
             ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            int len;
            byte[] buf = new byte[8192];
            while ((len = input.read(buf)) != -1)
            {
                baos.write(buf, 0, len);
            }
            return baos.toByteArray();
        }
        catch (IOException e)
        {

        }
        return new byte[0];
    }

    @Data
    private static class Item
    {
        @NotEmpty
        @NotBlank
        @NotNull
        private String name;

        @NotEmpty
        @NotBlank
        @NotNull
        private String value;
    }
}
