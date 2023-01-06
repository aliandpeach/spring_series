package com.base;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/09/09 14:16:44
 */
@Data
@AllArgsConstructor
public class Info
{
    private String id;

    private String name;

    private long total;
}
