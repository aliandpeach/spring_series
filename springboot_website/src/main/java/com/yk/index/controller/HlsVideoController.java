package com.yk.index.controller;

import com.yk.index.service.VideoServiceImpl;
import com.yk.index.ffmpeg.FFmpegProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/video/hls")
public class HlsVideoController
{
    private static final Logger logger = LoggerFactory.getLogger(HlsVideoController.class);

    @Value("${upload.to-path}")
    private String toPath;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private VideoServiceImpl videoService;

    @Autowired
    private FFmpegProcessor fFmpegProcessor;

    /**
     * 目录路径,这个路径需要包含test.info文件，test.key文件和test.mp4文件
     */
    private static final String PATH = "D:\\test\\";

    @GetMapping("/watch/${id}")
    public void watch(@PathVariable String id) throws Exception
    {
        FileInputStream inputStream = new FileInputStream(PATH + "test.mp4");
        String m3u8Url = "http://localhost:8080/upload/test.m3u8";
        String infoUrl = "http://localhost:8080/preview/test.info";
        StringBuilder url = new StringBuilder();
        url.append("/").append(request.getContextPath()).append("/video/hls/play").append(id).append("-%d.ts");
        fFmpegProcessor.convertMediaToM3u8ByHttp(inputStream, m3u8Url, infoUrl, url.toString());
    }

    @PostMapping("/play/${id}")
    public void play(@PathVariable String id) throws IOException
    {
    }
}
