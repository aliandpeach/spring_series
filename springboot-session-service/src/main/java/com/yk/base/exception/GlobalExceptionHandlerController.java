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

@RestControllerAdvice
public class GlobalExceptionHandlerController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandlerController.class);

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

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CustomException handleAuthenticationException(HttpServletResponse res, AccessDeniedException e) throws IOException
    {
//        res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
        LOGGER.error("auth failed", e);
        return new CustomException("auth failed", HttpStatus.FORBIDDEN);
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
