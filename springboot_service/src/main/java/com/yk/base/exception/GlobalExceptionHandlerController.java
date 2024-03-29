package com.yk.base.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandlerController.class);

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public BaseResponse<Map<String, Object>> handleCustomException(HttpServletResponse res, CustomException ex) throws IOException
    {
//        res.sendError(ex.getHttpStatus().value(), ex.getMessage());
        return new BaseResponse<>(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse<Map<String, Object>> handleAccessDeniedException(HttpServletResponse res, AccessDeniedException e) throws IOException
    {
//        res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
        LOGGER.error("Access denied", e);
        return new BaseResponse<>(ResponseCode.UNAUTHORIZED_EXCEPTION.code, "Access denied");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse<Map<String, Object>> handleAuthenticationException(HttpServletResponse res, AccessDeniedException e)
    {
        LOGGER.error("auth failed", e);
        return new BaseResponse<>(ResponseCode.AUTHENTICATION_EXCEPTION.code, "authentication failed");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Map<String, Object>> handleException(HttpServletResponse res, Exception e) throws IOException
    {
//        res.sendError(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
        LOGGER.error("Something went wrong", e);
        return new BaseResponse<>(ResponseCode.UNKNOWN_ERROR.code, ResponseCode.UNKNOWN_ERROR.message);
    }
}
