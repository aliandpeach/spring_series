package com.yk.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/12/17 15:02:16
 */
@Controller
@Slf4j
public class ServiceErrorController implements ErrorController
{
    private static final String ERROR_PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    @Override
    public String getErrorPath()
    {
        return ERROR_PATH;
    }

    @Bean
    public ErrorAttributes errorAttributes()
    {
        return new DefaultErrorAttributes()
        {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options)
            {
                return super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.EXCEPTION));
            }
        };
    }

    /**
     * web页面错误处理
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public ModelAndView errorPageHandler(HttpServletRequest request, HttpServletResponse response)
    {
        HttpStatus status = getStatus(request);
        response.setStatus(status.value());
        ServletWebRequest requestAttributes = new ServletWebRequest(request);
        Map<String, Object> attr = errorAttributes.getErrorAttributes(requestAttributes, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.EXCEPTION));
        return new ModelAndView("error/".concat(String.valueOf(status.value())), "errors", errorAttributes);
    }

    /**
     * 除web页面外的错误处理，比如json/xml等
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public BaseResponse<Map<String, Object>> errorApiHandler(HttpServletRequest request, HttpServletResponse response)
    {
        Map<String, Object> attr = getErrorAttributes(request, response);
        HttpStatus status = getStatus(request);
        response.setStatus(status.value());
        return new BaseResponse<>(Integer.valueOf(attr.getOrDefault("code", "0").toString()),
                String.valueOf(attr.getOrDefault("message", "")),
                new HashMap<>());
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, HttpServletResponse response)
    {
        WebRequest requestAttributes = new ServletWebRequest(request);
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(requestAttributes,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.EXCEPTION));

        // 从errorAttributes中获取异常信息, errorAttributes中的异常信息实际也是从request中获取的
        Object obj = errorAttributes.get("message");
        String exception = String.valueOf(errorAttributes.get("exception"));

        // 从request中直接获取异常信息, request中的异常信息是由tomcat在 requestDispatcher.forward跳转时设置的, 猜测是在StandardHostValue类中
        Object statusObject = request.getAttribute("javax.servlet.error.status_code");
        Object uri = request.getAttribute("javax.servlet.error.request_uri");
        Object statusObject1 = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exceptionObject = request.getAttribute("javax.servlet.error.exception");

        Throwable ex = this.errorAttributes.getError(requestAttributes);

        if (ex instanceof CustomException)
        {
            log.error("customer exception：[{}]", ex.getMessage());
            errorAttributes.put("message", ex.getMessage());
            errorAttributes.put("code", ((CustomException) ex).getCode());
        }
        else if (ex instanceof BindException)
        {
            errorAttributes.put("message", ResponseCode.BIND_EXCEPTION.message);
            errorAttributes.put("code", ResponseCode.BIND_EXCEPTION.code);
        }
        else if (ex instanceof DataIntegrityViolationException)
        {
            log.error("字段验证错误：[{}]", ex.getMessage());
            errorAttributes.put("message", ResponseCode.DATA_INTEGRITY_VIOLATION.message);
            errorAttributes.put("code", ResponseCode.DATA_INTEGRITY_VIOLATION.code);
        }
        else if (ex instanceof DataAccessException)
        {
            log.error("数据访问异常，异常信息：[{}]", ex.getMessage());
            errorAttributes.put("message", ResponseCode.DATA_ACCESS_EXCEPTION.message);
            errorAttributes.put("code", ResponseCode.DATA_ACCESS_EXCEPTION.code);
        }
        // api请求数据参数异常
        else if (ex instanceof HttpMessageNotReadableException)
        {
            log.error("请求数据参数异常，异常信息：[{}]", ex.getMessage());
            errorAttributes.put("message", ResponseCode.HTTP_MESSAGE_NOT_READABLE.message);
            errorAttributes.put("code", ResponseCode.HTTP_MESSAGE_NOT_READABLE.code);
        }
        else if (ex instanceof MissingServletRequestParameterException)
        {
            log.error("字段验证错误：[{}]", ex.getMessage());
            errorAttributes.put("message", ResponseCode.MISSING_SERVLET_REQUEST_PARAMETER.message);
            errorAttributes.put("code", ResponseCode.MISSING_SERVLET_REQUEST_PARAMETER.code);
        }
        else if (ex instanceof MethodArgumentNotValidException)
        {
            log.error("参数校验异常，异常信息：[{}]", ex.getMessage());
            errorAttributes.put("message", ResponseCode.METHOD_ARGUMENT_NOT_VALID.message);
            errorAttributes.put("code", ResponseCode.METHOD_ARGUMENT_NOT_VALID.code);
        }
        else if (ex instanceof HttpMediaTypeNotSupportedException || ex instanceof HttpMediaTypeNotAcceptableException)
        {
            log.error("媒体类型异常，异常信息：[{}]", ex.getMessage());
            errorAttributes.put("message", ResponseCode.HTTP_MEDIA.message);
            errorAttributes.put("code", ResponseCode.HTTP_MEDIA.code);
        }
        else if (ex instanceof ServletException)
        {
            ServletException e = (ServletException) ex;
            log.error("ServletException：[{}]", ex.getMessage());
            Throwable cause = e.getCause();
            if (cause instanceof CustomException)
            {
                errorAttributes.put("message", ((CustomException) cause).getMessage());
                errorAttributes.put("code", ((CustomException) cause).getCode());
            }
            else
            {
                errorAttributes.put("message", ResponseCode.SERVLET_EXCEPTION.message);
                errorAttributes.put("code", ResponseCode.SERVLET_EXCEPTION.code);
            }
        }
        else if (ex instanceof ValidationException)
        {
            log.error("ValidationException：[{}]", ex.getMessage());
            errorAttributes.put("message", ResponseCode.VALIDATION_EXCEPTION.message);
            errorAttributes.put("code", ResponseCode.VALIDATION_EXCEPTION.code);
        }
        else if (ex instanceof AccessDeniedException)
        {
            log.error("AccessDeniedException：[{}]", ex.getMessage());
            errorAttributes.put("message", ResponseCode.UNAUTHORIZED_EXCEPTION.message);
            errorAttributes.put("code", ResponseCode.UNAUTHORIZED_EXCEPTION.code);
        }
        else if (ex instanceof AuthenticationException)
        {
            log.error("AuthenticationException：[{}]", ex.getMessage());
            errorAttributes.put("message", ResponseCode.AUTHENTICATION_EXCEPTION.message);
            errorAttributes.put("code", ResponseCode.AUTHENTICATION_EXCEPTION.code);
        }
        else
        {
            if (null == ex)
            {
                log.error("系统内部异常，请查看日志处理 status={}, uri={}", statusObject, uri);
                errorAttributes.put("message", ResponseCode.URL_OR_PAGE_NOT_FOUND.message);
                errorAttributes.put("code", ResponseCode.URL_OR_PAGE_NOT_FOUND.code);
            }
            else
            {
                log.error("系统内部异常，请查看日志处理", ex);
                errorAttributes.put("message", ResponseCode.UNKNOWN_ERROR.message);
                errorAttributes.put("code", ResponseCode.UNKNOWN_ERROR.code);
            }
        }
        return errorAttributes;
    }

    private HttpStatus getStatus(HttpServletRequest request)
    {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null)
        {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try
        {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception ex)
        {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
