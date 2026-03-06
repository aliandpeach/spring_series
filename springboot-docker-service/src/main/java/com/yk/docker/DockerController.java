package com.yk.docker;

import com.yk.base.exception.DockerException;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/02/21 12:03:48
 */
@Controller
@RequestMapping("/docker")
public class DockerController
{

    @RequestMapping("/query")
    @ResponseBody
    public ResponseEntity<String> query()
    {
        throw new DockerException("controller error", 403);
//        return ResponseEntity.ok("OK");
    }

    @RequestMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> upload(@RequestPart("upload_file") MultipartFile file, HttpServletRequest request)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String destPath = request.getServletContext().getRealPath("/") + uuid + File.separator + file.getOriginalFilename();
        new File(destPath).getParentFile().mkdirs();
        try
        {
            file.transferTo(new File(destPath));
        }
        catch (IOException e)
        {
            throw new DockerException("upload file error", 400);
        }
        return ResponseEntity.ok("OK");
    }


    @Data
    public static class IndexModel
    {
        private String name;
        private String id;
        private List<String> indexList;
        private boolean active;
    }

    /**
     * ByteArrayHttpMessageConverter
     *
     * indexModel在form-data中以json格式传入:
     *
     * POST /demo/request/part/object HTTP/1.1
     * User-Agent: PostmanRuntime/7.26.8
     * Accept:
     * Postman-Token: 064a8c62-3dee-4143-b4b2-15ff3113458c
     * Host: 192.168.31.158:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: multipart/form-data; boundary=--------------------------777582379408141727964937
     * Content-Length: 219
     *
     * ----------------------------777582379408141727964937
     * Content-Disposition: form-data; name="file"; filename="test.txt"
     * binary
     *
     * ----------------------------777582379408141727964937
     * Content-Disposition: form-data; name="indexModel"
     * Content-Type: application/json
     *
     * {"id": "1", "name":"2"}
     * ----------------------------777582379408141727964937--
     */
    @RequestMapping("/transfer/0")
    @ResponseBody
    // (@RequestPart file File, @RequestPart Entity obj)用于同时提交文件+json
    // 若不想使用json, 则改为 (@RequestPart file file, Entity obj)
    // @RequestPart不应和@RequestBody同时使用, 会出现转换错误的问题
    public ResponseEntity<byte[]> transfer(@RequestPart("file") MultipartFile file,
                                           @RequestPart("indexModel") IndexModel indexModel,
                                           HttpServletRequest request,
                                           HttpServletResponse response)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String destPath = request.getServletContext().getRealPath("/") + uuid + File.separator + file.getOriginalFilename();
        new File(destPath).getParentFile().mkdirs();
        try (InputStream input = file.getInputStream())
        {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM.toString());
            return new ResponseEntity<>(IOUtils.toByteArray(input), headers, HttpStatus.OK);
        }
        catch (IOException e)
        {
            throw new DockerException("upload file error", 400);
        }
    }

    /**
     * transferR 和 transfer 只有参数 IndexModel 的差别
     *
     * indexModel在form-data中分别传入 name 和 id:
     *
     * POST /demo/request/object HTTP/1.1
     * User-Agent: PostmanRuntime/7.26.8
     * Accept:
     * Postman-Token: 50b3a5ac-6aa4-4f76-ab6d-f3132b0c4dce
     * Host: 192.168.31.158:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: multipart/form-data; boundary=--------------------------817472830912193668206114
     * Content-Length: 262
     *
     *  ----------------------------817472830912193668206114
     * Content-Disposition: form-data; name="file"; filename="test.txt"
     * binary
     *
     * ----------------------------817472830912193668206114
     * Content-Disposition: form-data; name="name"
     *
     * 2
     * ----------------------------817472830912193668206114
     * Content-Disposition: form-data; name="id"
     *
     * 3
     * ----------------------------817472830912193668206114--
     */
    @RequestMapping("/transfer/1")
    @ResponseBody
    public ResponseEntity<byte[]> transferR(@RequestPart("file") MultipartFile file,
                                           IndexModel indexModel,
                                           HttpServletRequest request,
                                           HttpServletResponse response)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String destPath = request.getServletContext().getRealPath("/") + uuid + File.separator + file.getOriginalFilename();
        new File(destPath).getParentFile().mkdirs();
        try (InputStream input = file.getInputStream())
        {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM.toString());
            return new ResponseEntity<>(IOUtils.toByteArray(input), headers, HttpStatus.OK);
        }
        catch (IOException e)
        {
            throw new DockerException("upload file error", 400);
        }
    }

    // 接口直接返回byte[] 需要指定返回类型为 MediaType.APPLICATION_OCTET_STREAM_VALUE, 不加的话返回类型是application/json需要请求端自己去指定解析类
    @RequestMapping(value = "/transfer/2")
//    @RequestMapping(value = "/transfer/2", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    // 不加ResponseBody会有异常: "Unknown return value type: [B"
    @ResponseBody
    public byte[] transfer2(@RequestPart("file") MultipartFile file,
                            @RequestPart("indexModel") IndexModel indexModel,
                            HttpServletRequest request,
                            HttpServletResponse response)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String destPath = request.getServletContext().getRealPath("/") + uuid + File.separator + file.getOriginalFilename();
        new File(destPath).getParentFile().mkdirs();
        try (InputStream input = file.getInputStream())
        {
            response.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM.toString());
            return IOUtils.toByteArray(input);
        }
        catch (IOException e)
        {
            throw new DockerException("upload file error", 400);
        }
    }

    /**
     * ByteArrayHttpMessageConverter
     */
    @RequestMapping("/transfer/3")
    @ResponseBody
    public void transfer3(@RequestPart("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String destPath = request.getServletContext().getRealPath("/") + uuid + File.separator + file.getOriginalFilename();
        new File(destPath).getParentFile().mkdirs();
        try (InputStream input = file.getInputStream(); OutputStream output = response.getOutputStream();
             BufferedOutputStream out = new BufferedOutputStream(output))
        {
            int len;
            byte[] buf = new byte[8192 * 100];
            while ((len = input.read(buf)) != -1)
            {
                out.write(buf, 0, len);
            }
        }
        catch (IOException e)
        {
            throw new DockerException("upload file error", 400);
        }
    }

    /**
     * 要使用纯对象对位Controller的参数, 参数内部最好是简单的数字或者字符串对象, 如果是复杂对象如: List<String>，最好还是使用@RequestPart或者@RequestBody
     */
    @RequestMapping("/transfer/4")
    @ResponseBody
    public ResponseEntity<IndexModel> transfer4(IndexModel indexModel)
    {
        return new ResponseEntity<>(indexModel, HttpStatus.OK);
    }

    @RequestMapping(value = "/transfer/5", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<IndexModel> transfer5(@RequestPart IndexModel indexModel)
    {
        return new ResponseEntity<>(indexModel, HttpStatus.OK);
    }

    @RequestMapping(value = "/transfer/6", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<IndexModel> transfer6(@RequestBody IndexModel indexModel)
    {
        return new ResponseEntity<>(indexModel, HttpStatus.OK);
    }

    // 特殊参数
    @RequestMapping(value = "/transfer/7", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<String>> transfer7(@RequestBody List<String> idList)
    {
        return new ResponseEntity<>(idList, HttpStatus.OK);
    }

    /**
     * 不要使用List<String> idList作为参数, restTemplate和前端请求无法解析
     */
    @RequestMapping(value = "/transfer/8", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<String>> transfer8(String[] idList)
    {
        return new ResponseEntity<>(Arrays.stream(Optional.ofNullable(idList).orElse(new String[0])).collect(Collectors.toList()), HttpStatus.OK);
    }

    @RequestMapping(value = "/transfer/8x", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<String>> transfer8x(@RequestParam List<String> idList)
    {
        return new ResponseEntity<>(idList, HttpStatus.OK);
    }

    @RequestMapping(value = "/transfer/9", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<String>> transfer9(@RequestPart List<String> idList)
    {
        return new ResponseEntity<>(idList, HttpStatus.OK);
    }

    // 特殊参数
    // 特殊参数
    @RequestMapping(value = "/transfer/10", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, String>> transfer10(@RequestBody Map<String, String> map)
    {
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * 不加注释目前看来无法接收参数
     */
    @RequestMapping(value = "/transfer/-11", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, String>> _transfer11(Map<String, String> map)
    {
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/transfer/11", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, String>> transfer11(@RequestParam Map<String, String> map)
    {
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/transfer/12", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, String>> transfer12(@RequestPart Map<String, String> map)
    {
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    // 特殊参数

    /*@PostMapping(value = "/transfer/13")
    @ResponseBody
    public String transfer13(@RequestParam(name = "name") String name,
                             @RequestParam(name = "age") Integer age)
    {
        return String.format("name = %s,age = %d", name, age);
    }*/

    @PostMapping(value = "/transfer/13")
    @ResponseBody
    public ResponseEntity<String> transfer13(@RequestParam(name = "name") String name,
                                             @RequestParam(name = "age") Integer age)
    {
        return new ResponseEntity<>(String.format("name = %s,age = %d", name, age), HttpStatus.OK);
    }

    /**
     * 似乎无法接收参数
     * RequestParamMethodArgumentResolver 没有能力解析复杂参数
     *
     *  之所以@RequestParam Map<String, String> 可以接收到是使用了 RequestParamMapMethodArgumentResolver解析
     */
    @PostMapping(path = "/transfer/14")
    public ResponseEntity<IndexModel> transfer14(@RequestParam(required = false) IndexModel indexModel)
    {
        return new ResponseEntity<>(indexModel, HttpStatus.OK);
    }

    /**
     * 似乎无法接收参数
     */
    @PostMapping(path = "/transfer/15")
    public ResponseEntity<List<IndexModel>> transfer15(@RequestParam(name = "indexModelList", required = false) List<IndexModel> indexModelList)
    {
        return new ResponseEntity<>(indexModelList, HttpStatus.OK);
    }
}


