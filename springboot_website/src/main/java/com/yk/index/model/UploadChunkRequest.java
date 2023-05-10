package com.yk.index.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
public class UploadChunkRequest
{
    private String id;

    // 第几个分片
    private int index;

    // 分片总数
    private int total;

    // // 分片固定长度(值是固定的, 这个长度指的不是当前分片的实际长度, 而是按多大的文件粒度分片的值)
    private long length;

    // 当前分片
    private MultipartFile file;

    private String fileName;
}
