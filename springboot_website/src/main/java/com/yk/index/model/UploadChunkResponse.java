package com.yk.index.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@Builder
@ToString
public class UploadChunkResponse
{
    private int index;

    private String md5;

    private String state;
}
