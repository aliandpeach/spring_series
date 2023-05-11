package com.yk.index.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class WebUploadChunkRequest implements Serializable
{
    private static final long serialVersionUID = 1542381054928202836L;
    
    @NotEmpty
    @NotBlank
    private String id;

    // 第几个分片
    @Min(0)
    @Max(999)
    private int chunk;

    // 分片总数
    @Min(1)
    @Max(999)
    private int chunks;

    // 文件总大小
    private long size;

    private Date lastModifiedDate;

    private String type;

    // 分片固定长度(值是固定的, 这个长度指的不是当前分片的实际长度, 而是按多大的文件粒度分片的值)
    private long sliceSize;

    // 当前分片
    private MultipartFile file;

    @NotBlank
    @NotEmpty
    private String name;

    // 上传到目标目录
    private String toPath;
}
