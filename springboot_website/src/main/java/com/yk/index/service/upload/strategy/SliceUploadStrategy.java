package com.yk.index.service.upload.strategy;

import com.yk.index.model.UploadChunkResponse;
import com.yk.index.model.WebUploadChunkRequest;

import java.io.IOException;

public interface SliceUploadStrategy
{
    UploadChunkResponse sliceUpload(WebUploadChunkRequest uploadChunkRequest) throws IOException, InterruptedException;
}
