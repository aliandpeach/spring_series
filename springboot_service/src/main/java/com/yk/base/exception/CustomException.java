package com.yk.base.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/17 11:40:16
 */
public class CustomException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final String message;
    private final HttpStatus httpStatus;
    private int status;

    public CustomException(String message, HttpStatus httpStatus)
    {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getStatus()
    {
        return httpStatus.value();
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public HttpStatus getHttpStatus()
    {
        return httpStatus;
    }

    @Override
    @JsonIgnore
    public StackTraceElement[] getStackTrace()
    {
        return super.getStackTrace();
    }

    @Override
    @JsonIgnore
    public synchronized Throwable getCause()
    {
        return super.getCause();
    }

    @Override
    @JsonIgnore
    public String getLocalizedMessage()
    {
        return super.getLocalizedMessage();
    }
}
