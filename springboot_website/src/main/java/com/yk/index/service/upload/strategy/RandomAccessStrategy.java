package com.yk.index.service.upload.strategy;

import com.yk.index.model.WebUploadChunkRequest;
import com.yk.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@UploadMode(mode = UploadModeEnum.RANDOM_ACCESS)
public class RandomAccessStrategy extends SliceUploadTemplate
{
    @Override
    public void upload(WebUploadChunkRequest webUploadChunkRequest, File dest) throws IOException
    {
        FileUtil.copyRandom(webUploadChunkRequest.getChunk() * webUploadChunkRequest.getSliceSize(),
                webUploadChunkRequest.getFile().getInputStream(), dest.getPath());
    }
}
