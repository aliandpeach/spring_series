//package com.yk.base.exception;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
////@RestControllerAdvice
//public class GlobalExceptionHandlerController
//{
//    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerController.class);
//
//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public BaseResponse<Map<String, Object>> handleException(HttpServletResponse res, Exception e) throws IOException
//    {
//        logger.error("Something went wrong", e);
//        return new BaseResponse<>(10000, "Something went wrong: " + e.getMessage(), new HashMap<>());
//    }
//
//    @ExceptionHandler(CustomException.class)
//    @ResponseBody
//    public BaseResponse<Map<String, Object>> handleCustomException(HttpServletResponse res, CustomException ex)
//    {
////        res.sendError(ex.getHttpStatus().value(), ex.getMessage());
//        return new BaseResponse<>(ex.getCode(), ex.getMessage(), new HashMap<>());
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    @ResponseBody
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public BaseResponse<Map<String, Object>> handleAccessDeniedException(HttpServletResponse res, AccessDeniedException e)
//    {
//        logger.error("Access denied", e);
//        return new BaseResponse<>(10010, "Access denied", new HashMap<>());
//    }
//
//    @ExceptionHandler(AuthenticationException.class)
//    @ResponseBody
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public BaseResponse<Map<String, Object>> handleAuthenticationException(HttpServletResponse res, AccessDeniedException e)
//    {
//        logger.error("auth failed", e);
//        return new BaseResponse<>(10011, "auth failed", new HashMap<>());
//    }
//}
