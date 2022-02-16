package com.yk.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DockerException
 */
@Data
@AllArgsConstructor
public class DockerException extends RuntimeException
{
    /**
     * 错误码
     */
    protected int status;
    /**
     * 错误信息
     */
    protected String message;

    public DockerException()
    {
        super();
    }

}
