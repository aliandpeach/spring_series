package com.yk.docker;

import com.yk.base.exception.DockerException;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/02/21 12:03:48
 */
@RestController
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

    @RequestMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> upload(@RequestPart("file") MultipartFile file, HttpServletRequest request)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String destPath = request.getServletContext().getRealPath("/") + uuid + File.separator + file.getName();
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
        private boolean responseContentType;
    }


    /**
     * ByteArrayHttpMessageConverter
     */
    @RequestMapping("/transfer")
    @ResponseBody
    public ResponseEntity<byte[]> transfer(@RequestPart("file") MultipartFile file,
                                           @RequestPart("indexModel") IndexModel indexModel,
                                           HttpServletRequest request,
                                           HttpServletResponse response)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String destPath = request.getServletContext().getRealPath("/") + uuid + File.separator + file.getName();
        new File(destPath).getParentFile().mkdirs();
        try (InputStream input = file.getInputStream())
        {
            HttpHeaders headers = new HttpHeaders();
            if (indexModel.responseContentType)
            {
                headers.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM.toString());
            }
            return new ResponseEntity<>(IOUtils.toByteArray(input), headers, HttpStatus.OK);
        }
        catch (IOException e)
        {
            throw new DockerException("upload file error", 400);
        }
    }

    @RequestMapping(value = "/transfer2", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ResponseBody
    public byte[] transfer2(@RequestPart("file") MultipartFile file,
                            @RequestPart("indexModel") IndexModel indexModel,
                            HttpServletRequest request,
                            HttpServletResponse response)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String destPath = request.getServletContext().getRealPath("/") + uuid + File.separator + file.getName();
        new File(destPath).getParentFile().mkdirs();
        try (InputStream input = file.getInputStream())
        {
            if (indexModel.responseContentType)
            {
                response.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM.toString());
            }
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
    @RequestMapping("/transfer3")
    @ResponseBody
    public void transfer3(@RequestPart("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response)
    {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String destPath = request.getServletContext().getRealPath("/") + uuid + File.separator + file.getName();
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
}
