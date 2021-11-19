package com.yk.base.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandlerController.class);

    @Bean
    public ErrorAttributes errorAttributes()
    {
        // Hide exception field in the return object
        return new DefaultErrorAttributes()
        {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options)
            {
                // 结果包含异常的message信息
                return super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE));
            }
        };
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomException handleCustomException(HttpServletResponse res, CustomException ex) throws IOException
    {
//        res.sendError(ex.getHttpStatus().value(), ex.getMessage());
        return new CustomException(ex.getMessage(), ex.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CustomException handleAccessDeniedException(HttpServletResponse res, AccessDeniedException e) throws IOException
    {
//        res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
        LOGGER.error("Access denied", e);
        return new CustomException("Access denied", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomException handleException(HttpServletResponse res, Exception e) throws IOException
    {
//        res.sendError(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
        LOGGER.error("Something went wrong", e);
        return new CustomException("Something went wrong: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
