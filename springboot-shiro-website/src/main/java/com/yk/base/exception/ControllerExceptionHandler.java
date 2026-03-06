package com.yk.base.exception;

import cn.hutool.core.text.StrBuilder;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<List<Map<String, String>>> bindException(BindException e)
    {
        BaseResponse<List<Map<String, String>>> baseResponse = handleBaseException(e);
        baseResponse.setMessage(ResponseCode.BIND_EXCEPTION.message);
        baseResponse.setCode(ResponseCode.BIND_EXCEPTION.code);

        List<Map<String, String>> list = new ArrayList<>();
        for (ObjectError objectError : e.getAllErrors())
        {
            Map<String, String> map = new HashMap<>();
            if (objectError instanceof FieldError)
            {
                FieldError fieldError = (FieldError) objectError;
                map.put("field", fieldError.getField());
                map.put("message", fieldError.getDefaultMessage());
            }
            else
            {
                map.put("field", objectError.getObjectName());
                map.put("message", objectError.getDefaultMessage());
            }
            list.add(map);
        }
        baseResponse.setData(list);
        return baseResponse;
    }

    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> servletException(ServletException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setMessage(e.getMessage());
        baseResponse.setCode(ResponseCode.SERVLET_EXCEPTION.code);
        if (e.getCause() instanceof ShiroException)
        {
            baseResponse.setMessage(((ShiroException) e.getCause()).getMessage());
            baseResponse.setCode(((ShiroException) e.getCause()).getCode());
            return baseResponse;
        }
        return baseResponse;
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> dataAccessException(DataAccessException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setMessage(e.getMessage());
        baseResponse.setCode(ResponseCode.DATA_ACCESS_EXCEPTION.code);
        return baseResponse;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> validationException(ValidationException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setMessage(e.getMessage());
        baseResponse.setCode(ResponseCode.VALIDATION_EXCEPTION.code);
        return baseResponse;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleDataIntegrityViolationException(DataIntegrityViolationException e)
    {
        BaseResponse<Map<String, String>> baseResponse = handleBaseException(e);
        baseResponse.setCode(ResponseCode.DATA_INTEGRITY_VIOLATION.code);
        if (e.getCause() instanceof ConstraintViolationException)
        {
            baseResponse.setMessage("字段验证错误，请完善后重试！");
            Set<ConstraintViolation<?>> set = ((ConstraintViolationException) e.getCause()).getConstraintViolations();
            baseResponse.setData(mapWithValidError(set));
            baseResponse.setCode(ResponseCode.CONSTRAINT_VIOLATION.code);
            return baseResponse;
        }
        baseResponse.setMessage("字段验证错误，请完善后重试！");
        return baseResponse;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setMessage(String.format("请求字段缺失, 类型为 %s，名称为 %s", e.getParameterType(), e.getParameterName()));
        baseResponse.setCode(ResponseCode.MISSING_SERVLET_REQUEST_PARAMETER.code);
        return baseResponse;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleConstraintViolationException(ConstraintViolationException e)
    {
        BaseResponse<Map<String, String>> baseResponse = handleBaseException(e);
        baseResponse.setCode(ResponseCode.CONSTRAINT_VIOLATION.code);
        baseResponse.setMessage("字段验证错误，请完善后重试！");
        baseResponse.setData(mapWithValidError(e.getConstraintViolations()));
        return baseResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        BaseResponse<Map<String, String>> baseResponse = handleBaseException(e);
        baseResponse.setCode(ResponseCode.METHOD_ARGUMENT_NOT_VALID.code);
        baseResponse.setMessage("字段验证错误，请完善后重试！");
        Map<String, String> errMap = mapWithFieldError(e.getBindingResult().getFieldErrors());
        baseResponse.setData(errMap);
        return baseResponse;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setCode(ResponseCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED.code);
        return baseResponse;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setCode(ResponseCode.HTTP_MEDIA.code);
        return baseResponse;
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public BaseResponse<?> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setCode(ResponseCode.HTTP_MEDIA.code);
        return baseResponse;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setCode(ResponseCode.HTTP_MESSAGE_NOT_READABLE.code);
        baseResponse.setMessage("缺失请求主体");
        return baseResponse;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public BaseResponse<?> handleNoHandlerFoundException(NoHandlerFoundException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setCode(ResponseCode.NO_HANDLER_FOUND.code);
        baseResponse.setMessage(ResponseCode.NO_HANDLER_FOUND.message);
        return baseResponse;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleUploadSizeExceededException(MaxUploadSizeExceededException e)
    {
        BaseResponse<Object> response = handleBaseException(e);
        response.setCode(ResponseCode.MAX_UPLOAD_SIZE_EXCEEDED.code);
        response.setMessage("当前请求超出最大限制：" + e.getMaxUploadSize() + " bytes");
        return response;
    }

    @ExceptionHandler(ShiroException.class)
    public BaseResponse<?> shiroException(ShiroException e)
    {
        BaseResponse<Object> baseResponse = handleBaseException(e);
        baseResponse.setCode(e.getCode());
        baseResponse.setMessage(e.getMessage());
        return baseResponse;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<?> handleGlobalException(Exception e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        baseResponse.setCode(ResponseCode.UNKNOWN_ERROR.code);
        baseResponse.setMessage("内部异常");
        return baseResponse;
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse<?> handleUnauthorizedException(UnauthorizedException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        logger.error("Unauthorized Exception {}", baseResponse.getMessage());
        baseResponse.setMessage("角色或者权限异常");
        baseResponse.setCode(ResponseCode.UNAUTHORIZED_EXCEPTION.code);
        return baseResponse;
    }

    @ExceptionHandler(value = UnauthenticatedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse<?> handleUnauthenticatedException(UnauthenticatedException e)
    {
        BaseResponse<?> baseResponse = handleBaseException(e);
        logger.error("Unauthenticated Exception {}", baseResponse.getMessage());
        baseResponse.setMessage("用户认证失败");
        baseResponse.setCode(ResponseCode.AUTHENTICATION_EXCEPTION.code);
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
        fieldErrors.forEach(filedError -> errMap.put(filedError.getField(), filedError.getDefaultMessage()));
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
