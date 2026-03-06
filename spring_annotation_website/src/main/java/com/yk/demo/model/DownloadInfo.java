package com.yk.demo.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 版本<dependency>
 *         <groupId>org.hibernate</groupId>
 *         <artifactId>hibernate-validator</artifactId>
 *         <version>5.1.0.Final</version>
 *     </dependency>
 *
 * NotBlank问题:
 * 使用javax.validation.constraints.NotBlank + @Validated 不能生效
 * 使用org.hibernate.validator.constraints.NotBlank + @Validated可以生效
 *
 * javax.validation.constraints.NotNull + @Validated 却可以生效
 *
 * javax.validation.constraints.NotBlank + @Valid 不能生效
 * 版本改为<dependency>
 *             <groupId>org.hibernate.validator</groupId>
 *             <artifactId>hibernate-validator</artifactId>
 *             <version>6.1.5.Final</version>
 *         </dependency>
 *
 * NotBlank等注释 + 设置groups + @Validated但不设置groups = 校验不会生效
 *
 * NotBlank等注释 + 不设置groups + @Validated设置groups = 只校验指定了相同groups的注释或者自定义注释,
 *                                                     若要使得不设置groups的注释/自定义注释生效, 需要采取以下两种方式的任一种
 *                                                     1. 若要使得不设置groups的注释/自定义注释生效, 给@Validated同时指定Default.class
 *                                                     2. 若要使得不设置groups的注释/自定义注释生效, 给@Validated指定的某个groups接口继承javax.validation.groups.Default
 *
 * NotBlank等注释 + @RequestParam 若要生效, 则需要在类级别上增加@Validated, 为防止@Validated注解对其他接口产生影响, 最好和NotBlank等注释指定相同的group
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
