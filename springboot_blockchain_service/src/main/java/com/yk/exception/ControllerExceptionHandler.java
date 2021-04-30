package com.yk.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ControllerExceptionHandler
 */

@RestControllerAdvice
public class ControllerExceptionHandler
{
    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleDataIntegrityViolationException(
            DataIntegrityViolationException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        if (e.getCause() instanceof ConstraintViolationException)
        {
            baseResponse = handleBaseException(e.getCause());
        }
        baseResponse.setMessage("字段验证错误，请完善后重试！");
        return baseResponse;
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setMessage(
                String.format("请求字段缺失, 类型为 %s，名称为 %s", e.getParameterType(), e.getParameterName()));
        return baseResponse;
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleConstraintViolationException(ConstraintViolationException e)
    {
        BaseResponse<Map<String, String>> baseResponse = handleBaseException(e);
        baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        baseResponse.setMessage("字段验证错误，请完善后重试！");
        baseResponse.setData(mapWithValidError(e.getConstraintViolations()));
        return baseResponse;
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e)
    {
        BaseResponse<Map<String, String>> baseResponse = handleBaseException(e);
        baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        baseResponse.setMessage("字段验证错误，请完善后重试！");
        Map<String, String> errMap =
                mapWithFieldError(e.getBindingResult().getFieldErrors());
        baseResponse.setData(errMap);
        return baseResponse;
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        return baseResponse;
    }
    
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public BaseResponse<?> handleHttpMediaTypeNotAcceptableException(
            HttpMediaTypeNotAcceptableException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
        return baseResponse;
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        baseResponse.setMessage("缺失请求主体");
        return baseResponse;
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public BaseResponse<?> handleNoHandlerFoundException(NoHandlerFoundException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        baseResponse.setStatus(status.value());
        baseResponse.setStatus(status.value());
        baseResponse.setMessage(status.getReasonPhrase());
        return baseResponse;
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleUploadSizeExceededException(MaxUploadSizeExceededException e)
    {
        BaseResponse<Object> response = handleBaseException(e);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("当前请求超出最大限制：" + e.getMaxUploadSize() + " bytes");
        return response;
    }
    
    @ExceptionHandler(BlockchainException.class)
    public BaseResponse<?> blockchainException(BlockchainException e)
    {
        BaseResponse<Object> baseResponse = handleBaseException(e);
        baseResponse.setStatus(e.getStatus());
        baseResponse.setData(e.getMessage());
        return baseResponse;
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<?> handleGlobalException(Exception e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        baseResponse.setStatus(status.value());
        baseResponse.setStatus(status.value());
        baseResponse.setMessage(status.getReasonPhrase());
        return baseResponse;
    }
    
    /**
     * 将字段验证错误转换为标准的map型，key:value = field:message
     *
     * @param fieldErrors 字段错误组
     * @return 如果返回null，则表示未出现错误
     */
    public static Map<String, String> mapWithFieldError(@Nullable List<FieldError> fieldErrors)
    {
        if (CollectionUtils.isEmpty(fieldErrors))
        {
            return Collections.emptyMap();
        }
        
        Map<String, String> errMap = new HashMap<>(4);
        fieldErrors.forEach(
                filedError -> errMap.put(filedError.getField(), filedError.getDefaultMessage()));
        return errMap;
    }
    
    private <T> BaseResponse<T> handleBaseException(Throwable t)
    {
        Assert.notNull(t, "Throwable must not be null");
        
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setMessage(t.getMessage());
        
//        logger.error("Captured an exception:", t);
        
        if (logger.isDebugEnabled())
        {
            baseResponse.setMessage(getStackTrace(t));
        }
        
        return baseResponse;
    }
    
    public static String getStackTrace(final Throwable throwable)
    {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
    
    /**
     * 将字段验证错误转换为标准的map型，key:value = field:message
     *
     * @param constraintViolations constraint violations(contain error information)
     * @return error detail map
     */
    @NonNull
    public static Map<String, String> mapWithValidError(
            Set<ConstraintViolation<?>> constraintViolations)
    {
        if (CollectionUtils.isEmpty(constraintViolations))
        {
            return Collections.emptyMap();
        }
        
        Map<String, String> errMap = new HashMap<>(4);
        // Format the error message
        constraintViolations.forEach(constraintViolation ->
                errMap.put(constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getMessage()));
        return errMap;
    }
}
