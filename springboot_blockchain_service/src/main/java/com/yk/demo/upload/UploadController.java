package com.yk.demo.upload;

import lombok.Data;
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
    @PostMapping("/upload/multiple")
    @ResponseBody
    public Map<String, List<String>> multipleUpload(@RequestPart(required = false, name = "fileName_1") MultipartFile[] files1,
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
        multiValueMaps.entrySet().forEach(t ->
        {
            t.getValue().forEach(f ->
            {
                try
                {
                    f.transferTo(new File("D:\\opt\\" + System.currentTimeMillis() + "_" + f.getOriginalFilename()));
                    result.get("success").add(f.getOriginalFilename());
                }
                catch (IOException e)
                {
                    result.get("failed").add(f.getOriginalFilename());
                }
            });
        });
//        String fileName = file[0].getOriginalFilename();
//        String filePath = "D:\\opt\\" + System.currentTimeMillis() + "_" + fileName;
//
//        File dest = new File(filePath);
//        Files.copy(file[0].getInputStream(), dest.toPath());
        return result;
    }
    
    @PostMapping("/upload/single")
    @ResponseBody
    public String singleUpload(MultipartHttpServletRequest request) throws
            IOException
    {
        MultipartFile uploadFile = request.getFile("_uploadSingle_one");
        long cur = System.currentTimeMillis();
        uploadFile.transferTo(new File("D:\\opt\\" + cur + "_" + uploadFile.getOriginalFilename()));
        return "SUCCESS:" + cur + "_" + uploadFile.getOriginalFilename();
    }
    
    @PostMapping("/upload/single2")
    @ResponseBody
    public String singleUpload2(@RequestPart("_uploadSingle_two") MultipartFile uploadFile) throws
            IOException
    {
        long cur = System.currentTimeMillis();
        uploadFile.transferTo(new File("D:\\opt\\" + cur + "_" + uploadFile.getOriginalFilename()));
        return "SUCCESS:" + cur + "_" + uploadFile.getOriginalFilename();
    }
    
    /**
     * 参数中同时包含了文件和application/json格式的数据
     *
     * 不能使用postman调用, 但可以使用jmeter 服务端SSL双向认证时, 如果使用Burp Suite抓包,则postman或者jmeter和Burp Suite都需要配置私钥证书(pkcs12即可)
     *
     * 指定标准的http 响应码：
     * 1. 使用ResponseEntity   ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>());
     * 2. HttpServletResponse.setStatus(201)
     * 3. 方法上加入注解：
     *    a. @ExceptionHandler(value = ArrayIndexOutOfBoundsException.class)
     *    b. @ResponseStatus(HttpStatus.BAD_REQUEST)
     * 4. 全局统一处理 ControllerExceptionHandler
     */
    @PostMapping("/upload/multiple/json")
    @ResponseBody
    public Map<String, List<String>> multipleUpload2(MultipartHttpServletRequest request,
                                                    @RequestPart(required = false, name = "params") Map<String, String> params,
                                                    HttpServletResponse response)
    {
        Map<String, List<String>> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        result.putIfAbsent("failed1", new ArrayList<>());
        result.putIfAbsent("success1", new ArrayList<>());
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File("D:\\opt\\" + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                result.get("success1").add(t.getValue().getOriginalFilename());
            }
            catch (IOException e)
            {
                result.get("failed1").add(t.getValue().getOriginalFilename());
            }
        });
        return result;
    }

    /**
     * 上传文件接口, 附带json格式参数, 可以使用postman调用 (params得到的结果是 {"params" : "value"} )
     */
    @PostMapping("/upload/multiple/json3")
    @ResponseBody
    public Map<String, List<String>> multipleUpload3(MultipartHttpServletRequest request, @RequestParam Map<String, String> params)
    {
        String value = params.get("params");
        Map<String, List<String>> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        result.putIfAbsent("failed1", new ArrayList<>());
        result.putIfAbsent("success1", new ArrayList<>());
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File("D:\\opt\\" + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                result.get("success1").add(t.getValue().getOriginalFilename());
            }
            catch (IOException e)
            {
                result.get("failed1").add(t.getValue().getOriginalFilename());
            }
        });
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
     * Content-Disposition: form-data; name="level"
     *
     * 11111
     * ----------------------------153054330803524445015047--
     */
    @PostMapping("/upload/multiple/json4")
    @ResponseBody
    @Validated
    public Map<String, List<String>> multipleUpload4(MultipartHttpServletRequest request,
                                                     @NotEmpty(message = "level is empty") @RequestParam(value = "level", required = false) String level)
    {
        Map<String, List<String>> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        result.putIfAbsent("failed1", new ArrayList<>());
        result.putIfAbsent("success1", new ArrayList<>());
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File("D:\\opt\\" + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                result.get("success1").add(t.getValue().getOriginalFilename());
            }
            catch (IOException e)
            {
                result.get("failed1").add(t.getValue().getOriginalFilename());
            }
        });
        return result;
    }

    /**
     * 可以使用postman调用, 需要分别传入 item中的name和value属性
     *
     * POST /import/upload/multiple/json5 HTTP/1.1
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
     * 1
     * ----------------------------570002456370464510172609
     * Content-Disposition: form-data; name="value"
     *
     * 2
     * ----------------------------570002456370464510172609--
     */
    @PostMapping("/upload/multiple/json5")
    @ResponseBody
    public Map<String, List<String>> multipleUpload5(MultipartHttpServletRequest request, @Validated Item item)
    {
        Map<String, List<String>> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        result.putIfAbsent("failed1", new ArrayList<>());
        result.putIfAbsent("success1", new ArrayList<>());
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File("D:\\opt\\" + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                result.get("success1").add(t.getValue().getOriginalFilename());
            }
            catch (IOException e)
            {
                result.get("failed1").add(t.getValue().getOriginalFilename());
            }
        });
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
    @PostMapping("/upload/multiple/json6")
    @ResponseBody
    public Map<String, List<String>> multipleUpload6(MultipartHttpServletRequest request, @RequestPart List<Item> items)
    {
        Map<String, List<String>> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        result.putIfAbsent("failed1", new ArrayList<>());
        result.putIfAbsent("success1", new ArrayList<>());
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File("D:\\opt\\" + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                result.get("success1").add(t.getValue().getOriginalFilename());
            }
            catch (IOException e)
            {
                result.get("failed1").add(t.getValue().getOriginalFilename());
            }
        });
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
     * Content-Disposition: form-data; name="xxxx"
     *
     * -字符串参数-
     * ----------------------------038190145852481148403885--
     */
    @PostMapping("/upload/multiple/json7")
    @ResponseBody
    public Map<String, List<String>> multipleUpload7(MultipartHttpServletRequest request, @RequestPart(name = "name") String json)
    {
        Map<String, List<String>> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        result.putIfAbsent("failed1", new ArrayList<>());
        result.putIfAbsent("success1", new ArrayList<>());
        map.entrySet().stream().filter(t -> !t.getKey().equals("params")).forEach(t ->
        {
            try
            {
                t.getValue().transferTo(new File("D:\\opt\\" + System.currentTimeMillis() + "_" + t.getValue().getOriginalFilename()));
                result.get("success1").add(t.getValue().getOriginalFilename());
            }
            catch (IOException e)
            {
                result.get("failed1").add(t.getValue().getOriginalFilename());
            }
        });
        return result;
    }

    /**
     * var xhr = new XMLHttpRequest();
     * xhr.open("POST", '/import/upload/multiple/bytes', true);
     * xhr.onload = function (e) {
     * if (xhr.status === 200) {
     *        debugger
     *        var result = JSON.parse(this.responseText);
     *        console.log(result);
     *    }
     * };
     * var blob = new Blob(['abc123'], {type: 'text/plain'});
     * xhr.send(blob);
     *
     * @param bytes
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/upload/multiple/bytes")
    @ResponseBody
    public Map<String, String> bytes(@RequestBody byte[] bytes) throws
            IOException
    {
        String string = new String(bytes, StandardCharsets.ISO_8859_1);
        String string2 = new String(bytes, StandardCharsets.UTF_8);
        try (ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
             BufferedInputStream buffered = new BufferedInputStream(bai);
             InputStreamReader reader = new InputStreamReader(buffered, StandardCharsets.UTF_8);
             StringWriter sw = new StringWriter())
        {
            int len = 0;
            char[] buffer = new char[8092];
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
    public void download(HttpServletRequest request, HttpServletResponse response)
    {
        String downloadName = request.getParameter("download.name");
        try (InputStream input = new FileInputStream(downloadName);
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
    
    @PostMapping(value = "/downloadBlob", produces = "application/octet-stream")
    @ResponseBody
    public byte[] downloadBlob(@RequestBody Map<String, String> params)
    {
        String filePath = params.get("download.name");
        try (InputStream input = new FileInputStream(filePath);
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
