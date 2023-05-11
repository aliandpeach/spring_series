package com.yk.index.service.upload.strategy;

import com.yk.index.model.WebUploadChunkRequest;
import com.yk.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@UploadMode(mode = UploadModeEnum.MAPPED_BYTEBUFFER)
public class MappedByteBufferStrategy extends SliceUploadTemplate
{
    @Override
    public void upload(WebUploadChunkRequest uploadChunkRequest, File dest) throws IOException
    {
        FileUtil.copy(uploadChunkRequest.getChunk() * uploadChunkRequest.getSliceSize(),
                uploadChunkRequest.getFile().getInputStream(), dest.getPath());
    }
}
