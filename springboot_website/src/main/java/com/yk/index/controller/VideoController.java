package com.yk.index.controller;

import com.yk.index.model.UploadChunkRequest;
import com.yk.index.model.UploadChunkResponse;
import com.yk.index.model.WebUploadChunkRequest;
import com.yk.io.FileUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/video")
public class VideoController
{
    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView view()
    {
        return new ModelAndView("video");
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<UploadChunkResponse> upload(MultipartHttpServletRequest request, UploadChunkRequest uploadChunkRequest) throws IOException, InterruptedException
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
        String root = request.getServletContext().getRealPath("/");

        synchronized (uploadChunkRequest.getId().intern())
        {
            String destTmpFile = root + File.separator + uploadChunkRequest.getId() + "_tmp_" + new File(uploadChunkRequest.getFileName());
            FileUtil.copy(uploadChunkRequest.getIndex() * uploadChunkRequest.getLength(),
                    uploadChunkRequest.getFile().getInputStream(),
                    destTmpFile);

            String destCompleteFile = root + File.separator + uploadChunkRequest.getId() + "_mark.conf";
            FileUtil.copyRandom(uploadChunkRequest.getIndex(),
                    new ByteArrayInputStream(new byte[]{Byte.MAX_VALUE}),
                    destCompleteFile);

            byte[] completeMarks = FileUtils.readFileToByteArray(new File(destCompleteFile));
            byte complete = Byte.MAX_VALUE;
            for (int i = 0; i < completeMarks.length && complete == Byte.MAX_VALUE; i++)
            {
                complete = (byte) (complete & completeMarks[i]);
            }
            if (completeMarks.length == uploadChunkRequest.getTotal() && complete == Byte.MAX_VALUE)
            {
                cn.hutool.core.io.FileUtil.rename(new File(destTmpFile), uploadChunkRequest.getFileName(), false, true);
                logger.debug("mark file delete {}", new File(destCompleteFile).delete());
                return ResponseEntity.ok(new UploadChunkResponse
                        (uploadChunkRequest.getIndex(),
                                DigestUtils.md5Hex(uploadChunkRequest.getFile().getBytes()), "true"));
            }
        }

        return ResponseEntity.ok(new UploadChunkResponse
                (uploadChunkRequest.getIndex(),
                DigestUtils.md5Hex(uploadChunkRequest.getFile().getBytes()), "false"));
    }

    @RequestMapping(value = "/webuploader", method = RequestMethod.POST)
    public ResponseEntity<UploadChunkResponse> webuploader(WebUploadChunkRequest webUploadChunkRequest) throws IOException, InterruptedException
    {
        long fileSize = webUploadChunkRequest.getFile().getSize();
        String originalFilename = webUploadChunkRequest.getFile().getOriginalFilename();
        logger.debug("originalFilename {}, fileSize {}", originalFilename, fileSize);
        String root = request.getServletContext().getRealPath("/");

        synchronized (webUploadChunkRequest.getId().intern())
        {
            String destTmpFile = root + File.separator + webUploadChunkRequest.getId() + "_tmp_" + new File(webUploadChunkRequest.getName());
            FileUtil.copy(webUploadChunkRequest.getChunk() * webUploadChunkRequest.getSliceSize(),
                    webUploadChunkRequest.getFile().getInputStream(),
                    destTmpFile);

            String destCompleteFile = root + File.separator + webUploadChunkRequest.getId() + "_mark.conf";
            FileUtil.copyRandom(webUploadChunkRequest.getChunk(),
                    new ByteArrayInputStream(new byte[]{Byte.MAX_VALUE}),
                    destCompleteFile);

            byte[] completeMarks = FileUtils.readFileToByteArray(new File(destCompleteFile));
            byte complete = Byte.MAX_VALUE;
            for (int i = 0; i < completeMarks.length && complete == Byte.MAX_VALUE; i++)
            {
                complete = (byte) (complete & completeMarks[i]);
            }
            if (completeMarks.length == webUploadChunkRequest.getChunks() && complete == Byte.MAX_VALUE)
            {
                cn.hutool.core.io.FileUtil.rename(new File(destTmpFile), webUploadChunkRequest.getName(), false, true);
                logger.debug("mark file delete {}", new File(destCompleteFile).delete());
                return ResponseEntity.ok(new UploadChunkResponse(
                        webUploadChunkRequest.getChunk(),
                        DigestUtils.md5Hex(webUploadChunkRequest.getFile().getBytes()), "true"));
            }
        }

        return ResponseEntity.ok(new UploadChunkResponse(
                webUploadChunkRequest.getChunk(),
                DigestUtils.md5Hex(webUploadChunkRequest.getFile().getBytes()), "false"));
    }

    @RequestMapping("/play/{id}")
    public void play(@PathVariable String id)
    {
    }
}
