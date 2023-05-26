package com.yk.index.controller;

import com.yk.index.model.UploadChunkResponse;
import com.yk.index.model.WebUploadChunkRequest;
import com.yk.index.service.VideoServiceImpl;
import com.yk.io.FileUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/video/dash")
public class DashVideoController
{
    private static final Logger logger = LoggerFactory.getLogger(DashVideoController.class);

    @Value("${upload.to-path}")
    private String toPath;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private VideoServiceImpl videoService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView view()
    {
        return new ModelAndView("video");
    }

    @RequestMapping("/watch/{id}")
    public ModelAndView watch(@PathVariable String id)
    {
        return new ModelAndView("play").addObject("source", "/video/play/" + id);
    }

    @RequestMapping("/play/{id}")
    public void play(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        File file = new File(request.getServletContext().getRealPath(toPath) + File.separator + "123.mp4");
        logger.debug("id : {}, file path : {}", id, file.getPath());
        logger.debug("ServletPath : {}", request.getServletPath());
        try (InputStream input = new FileInputStream(file);
             BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream()))
        {
            response.setContentType("video/mp4");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + "123.mp4" + "\"");
            response.setContentLength(input.available());
            response.setHeader("Content-Range", "" + (input.available() - 1));
            response.setHeader("Accept-Ranges", "bytes");
            byte[] data = new byte[2 * 1024 * 1024];
            int len;
            while ((len = input.read(data)) != -1)
            {
                outputStream.write(data, 0, len);
            }
            outputStream.flush();
            response.flushBuffer();
        }
    }
}
