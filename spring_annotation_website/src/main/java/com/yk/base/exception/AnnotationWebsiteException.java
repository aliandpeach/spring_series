package com.yk.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/28 14:53:15
 */
@Data
@AllArgsConstructor
public class AnnotationWebsiteException extends RuntimeException
{
    private static final long serialVersionUID = -4174507779439832970L;

    private int code;

    private String message;
}