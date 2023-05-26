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
@RequestMapping("/video")
public class VideoController
{
    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

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

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<UploadChunkResponse> upload(MultipartHttpServletRequest request, WebUploadChunkRequest uploadChunkRequest) throws IOException, InterruptedException
    {
        MultiValueMap<String, MultipartFile> files = request.getMultiFileMap();

        // ======= UploadChunkRequest中设置了MultipartFile, 这部分可以不需要 =======
        for (Map.Entry<String, List<MultipartFile>> file : files.entrySet())
        {
            List<MultipartFile> list = file.getValue();
            for (MultipartFile _file : list)
            {
                String _originalFilename = System.currentTimeMillis() + "_" + _file.getOriginalFilename();
                String _name = System.currentTimeMillis() + "_" + _file.getName();
                long _len = _file.getSize();
                TimeUnit.MILLISECONDS.sleep(1);
                logger.debug("file _originalFilename : {}, length {}, name {}, parma : {}", _originalFilename, _len, _name, uploadChunkRequest);
            }
        }
        // ======= UploadChunkRequest中设置了MultipartFile, 这部分可以不需要 =======

        long fileSize = uploadChunkRequest.getFile().getSize();
        String originalFilename = uploadChunkRequest.getFile().getOriginalFilename();
        logger.debug("originalFilename {}, fileSize {}", originalFilename, fileSize);
        String root = request.getServletContext().getRealPath(toPath);

        synchronized (uploadChunkRequest.getId().intern())
        {
            String destTmpFile = root + File.separator + uploadChunkRequest.getId() + "_tmp_" + uploadChunkRequest.getName();
            new File(destTmpFile).getParentFile().mkdirs();
            FileUtil.copy(uploadChunkRequest.getChunk() * uploadChunkRequest.getSliceSize(),
                    uploadChunkRequest.getFile().getInputStream(),
                    destTmpFile);

            String destCompleteFile = root + File.separator + uploadChunkRequest.getId() + "_mark.conf";
            FileUtil.copyRandom(uploadChunkRequest.getChunk(),
                    new ByteArrayInputStream(new byte[]{Byte.MAX_VALUE}),
                    destCompleteFile);

            byte[] completeMarks = FileUtils.readFileToByteArray(new File(destCompleteFile));
            byte complete = Byte.MAX_VALUE;
            for (int i = 0; i < completeMarks.length && complete == Byte.MAX_VALUE; i++)
            {
                complete = (byte) (complete & completeMarks[i]);
            }
            if (completeMarks.length == uploadChunkRequest.getChunks() && complete == Byte.MAX_VALUE)
            {
                cn.hutool.core.io.FileUtil.rename(new File(destTmpFile), uploadChunkRequest.getName(), false, true);
                logger.debug("mark file delete {}", new File(destCompleteFile).delete());
                return ResponseEntity.ok(new UploadChunkResponse
                        (uploadChunkRequest.getChunk(),
                                DigestUtils.md5Hex(uploadChunkRequest.getFile().getBytes()), true));
            }
        }

        return ResponseEntity.ok(new UploadChunkResponse
                (uploadChunkRequest.getChunk(),
                        DigestUtils.md5Hex(uploadChunkRequest.getFile().getBytes()), false));
    }

    @RequestMapping(value = "/webuploader", method = RequestMethod.POST)
    public ResponseEntity<UploadChunkResponse> webuploader(WebUploadChunkRequest webUploadChunkRequest)
            throws IOException, InterruptedException, ExecutionException
    {
        long fileSize = webUploadChunkRequest.getFile().getSize();
        String originalFilename = webUploadChunkRequest.getFile().getOriginalFilename();
        logger.debug("originalFilename {}, fileSize {}", originalFilename, fileSize);
        String root = request.getServletContext().getRealPath(toPath);
        webUploadChunkRequest.setToPath(root);
        return ResponseEntity.ok(videoService.sliceUpload(webUploadChunkRequest));
    }

    @RequestMapping("/play/test")
    public ModelAndView playTet()
    {
        return new ModelAndView("play").addObject("source", "/video/123.mp4");
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
