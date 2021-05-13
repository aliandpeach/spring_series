package com.yk.demo.upload;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     * @param request
     * @param params
     * @return
     * @throws IOException
     */
    @PostMapping("/upload/multiple/json")
    @ResponseBody
    public Map<String, List<String>> multipleUpload2(MultipartHttpServletRequest request,
                                                     @RequestPart(required = false, name = "params") Map<String, String> params) throws
            IOException
    {
        Map<String, List<String>> result = new HashMap<>();
        Map<String, MultipartFile> map = request.getFileMap();
        result.putIfAbsent("failed1", new ArrayList<>());
        result.putIfAbsent("success1", new ArrayList<>());
        map.entrySet().forEach(t ->
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
        String filePath = params.get("down.name");
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
}
