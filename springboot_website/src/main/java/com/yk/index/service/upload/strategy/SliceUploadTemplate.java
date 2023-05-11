package com.yk.index.service.upload.strategy;

import com.yk.index.model.UploadChunkResponse;
import com.yk.index.model.WebUploadChunkRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

@Slf4j
public abstract class SliceUploadTemplate implements SliceUploadStrategy
{
    private static final Logger logger = LoggerFactory.getLogger(SliceUploadTemplate.class);

    public abstract void upload(WebUploadChunkRequest uploadChunkRequest, File dest) throws IOException;

    protected File createTmpFile(WebUploadChunkRequest uploadChunkRequest)
    {
        String filePath = uploadChunkRequest.getToPath()
                + File.separator + uploadChunkRequest.getId()
                + "_tmp_" + uploadChunkRequest.getName();
        File _tmp = new File(filePath);
        if (!_tmp.getParentFile().exists())
        {
            logger.debug("create tmp {} result {}", filePath, _tmp.getParentFile().mkdirs());
        }
        return _tmp;
    }

    protected File createMarkFile(WebUploadChunkRequest uploadChunkRequest)
    {
        String markFile = uploadChunkRequest.getToPath() + File.separator + uploadChunkRequest.getId() + "_mark.conf";
        File mark = new File(markFile);
        if (!mark.getParentFile().exists())
        {
            logger.debug("create mark file {}, result {}", markFile, mark.getParentFile().mkdirs());
        }
        return mark;
    }

    @Override
    public UploadChunkResponse sliceUpload(WebUploadChunkRequest uploadChunkRequest) throws IOException, InterruptedException
    {
        try
        {
            File destTmpFile = createTmpFile(uploadChunkRequest);
            synchronized (uploadChunkRequest.getId().intern())
            {
                this.upload(uploadChunkRequest, createTmpFile(uploadChunkRequest));
                File markFile = createMarkFile(uploadChunkRequest);
                boolean state = this.checkUploadState(uploadChunkRequest, markFile);
                if (state)
                {
                    cn.hutool.core.io.FileUtil.rename(destTmpFile, uploadChunkRequest.getName(), false, true);
                    logger.debug("mark conf file delete {}", markFile.delete());
                }
                return UploadChunkResponse.builder()
                        .chunk(uploadChunkRequest.getChunk())
                        .state(state)
                        .md5(DigestUtils.md5Hex(uploadChunkRequest.getFile().getBytes())).build();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return UploadChunkResponse.builder()
                    .chunk(uploadChunkRequest.getChunk())
                    .state(false)
                    .md5(DigestUtils.md5Hex(uploadChunkRequest.getFile().getBytes())).build();
        }
    }

    /**
     * 检查并修改文件上传进度
     */
    public boolean checkUploadState(WebUploadChunkRequest uploadChunkRequest, File markFile)
    {
        byte complete = Byte.MIN_VALUE;
        try (RandomAccessFile accessFile = new RandomAccessFile(markFile, "rw"))
        {
            accessFile.setLength(uploadChunkRequest.getChunks());
            accessFile.seek(uploadChunkRequest.getChunk());
            accessFile.write(Byte.MAX_VALUE);

            byte[] completeList = FileUtils.readFileToByteArray(markFile);
            complete = Byte.MAX_VALUE;
            for (int i = 0; i < completeList.length && complete == Byte.MAX_VALUE; i++)
            {
                complete = (byte) (complete & completeList[i]);
            }
        }
        catch (IOException e)
        {
            log.error(e.getMessage(), e);
        }
        return complete == Byte.MAX_VALUE;
    }
}
