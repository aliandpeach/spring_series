package com.yk.demo.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/25 11:23:44
 */
@Data
public class DownloadInfo
{
    @NotEmpty(message = "文件链接为空")
    @NotBlank(message = "文件链接包含空白符")
    @Pattern(regexp = "(https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]")
    private String url;

    private String method;

    @NotEmpty(message = "策略ID为空")
    @NotBlank(message = "策略ID包含空白符")
    private String jobId;
}
