package com.yk.index.controller;

import com.yk.index.ffmpeg.FFmpegProcessor;
import com.yk.index.service.VideoServiceImpl;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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

    @GetMapping("/watch/{id}")
    public ModelAndView watch(@PathVariable String id) throws Exception
    {
        return new ModelAndView("hls").addObject("source", "/video/hls/play/1213");
    }

    @GetMapping("/play/{id}")
    public String play(@PathVariable String id) throws IOException
    {
        File file = new File(request.getServletContext().getRealPath(toPath) + File.separator + "123456789" + File.separator + "123.m3u8");
        try (FileInputStream input = new FileInputStream(file))
        {
            return IOUtils.toString(input);
        }
    }
}
