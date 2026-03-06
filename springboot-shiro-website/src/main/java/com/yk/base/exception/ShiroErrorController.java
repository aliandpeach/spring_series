package com.yk.base.exception;


import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.text.StrBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class ShiroErrorController implements ErrorController
{
    private static final String ERROR_PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;

    @Getter
    @Autowired(required = false)
    private HttpServletRequest request;

    @Getter
    @Autowired(required = false)
    private HttpServletResponse response;

    /**
     * 支持HTML的错误视图
     */
    @RequestMapping(value = ERROR_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = {MediaType.TEXT_HTML_VALUE})
    public ModelAndView page(HttpServletRequest request, HttpServletResponse response)
    {
        return buildPage(request, response);
    }

    /**
     * 支持JSON的错误视图
     */
    @RequestMapping(value = ERROR_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object body(HttpServletRequest request, HttpServletResponse response)
    {
        return buildBody(request, response);
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

    private ModelAndView buildPage(HttpServletRequest request, HttpServletResponse response)
    {
        Map<String, Object> errorAttributes = this.getErrorAttributes(request, response);
        HttpStatus status = getStatus(request);
        response.setStatus(status.value());
        return new ModelAndView("error/".concat(String.valueOf(status.value())), "errors", errorAttributes);
    }

    private BaseResponse<?> buildBody(HttpServletRequest request, HttpServletResponse response)
    {
        Map<String, Object> errorAttributes = this.getErrorAttributes(request, response);
        HttpStatus status = getStatus(request);
        response.setStatus(status.value());

        String message = String.valueOf(errorAttributes.get("message"));
        BaseResponse<?> baseResponse = new BaseResponse<>();
        baseResponse.setMessage(message);
        baseResponse.setCode((int) errorAttributes.getOrDefault("code", "0"));
        return baseResponse;
    }

    @Override
    public String getErrorPath()
    {
        return ERROR_PATH;
    }

    @Bean
    public ErrorAttributes errorAttributes()
    {
        // Hide exception field in the return object
        return new DefaultErrorAttributes()
        {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options)
            {
                Map<String, Object> attrs = super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.EXCEPTION));
                return attrs;
            }
        };
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, HttpServletResponse response)
    {

//        errorProperties.setIncludeException(true);
//        errorProperties.setIncludeMessage(ErrorProperties.IncludeAttribute.ALWAYS);
//        errorProperties.setIncludeStacktrace(ErrorProperties.IncludeAttribute.ALWAYS);

        WebRequest requestAttributes = new ServletWebRequest(request);
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(requestAttributes, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.EXCEPTION));

        // 从errorAttributes中获取异常信息, errorAttributes中的异常信息实际也是从request中获取的
        Object obj = errorAttributes.get("message");
        String exception = String.valueOf(errorAttributes.get("exception"));

        // 从request中直接获取异常信息, request中的异常信息是由tomcat在 requestDispatcher.forward跳转时设置的, 猜测是在StandardHostValue类中
        Object statusObject = request.getAttribute("javax.servlet.error.status_code");
        Object statusObject1 = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exceptionObject = request.getAttribute("javax.servlet.error.exception");

        log.error("errorAttributes exception {}, status {}", exceptionObject, statusObject);

        Throwable ex = this.errorAttributes.getError(requestAttributes);
        if (ex instanceof ShiroException)
        {
            log.error("customer exception：[{}]", ex.getMessage());
            errorAttributes.put("message", ex.getMessage());
            errorAttributes.put("code", ((ShiroException) ex).getCode());
        }
        else if (ex instanceof BindException)
        {
            BindException e = (BindException) ex;
            errorAttributes.put("message", ResponseCode.BIND_EXCEPTION.message);
            errorAttributes.put("code", ResponseCode.BIND_EXCEPTION.code);
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
        else if (ex instanceof MethodArgumentNotValidException)
        {
            log.error("异常：参数校验异常，异常信息：[{}]", ex.getMessage());
            errorAttributes.put("message", ResponseCode.METHOD_ARGUMENT_NOT_VALID.message);
            errorAttributes.put("code", ResponseCode.METHOD_ARGUMENT_NOT_VALID.code);
        }
        else if (ex instanceof HttpMediaTypeNotSupportedException || ex instanceof HttpMediaTypeNotAcceptableException)
        {
            errorAttributes.put("message", ResponseCode.HTTP_MEDIA.message);
            errorAttributes.put("code", ResponseCode.HTTP_MEDIA.code);
            log.error("媒体类型异常，异常信息：[{}]", ex.getMessage());
        }
        else if (ex instanceof ServletException)
        {
            // 某些组件提供的接口拦截器, 例如shiro, 抛出自定义异常, 会被set到 ServletException 的cause 中
            ServletException e = (ServletException) ex;
            Throwable cause = e.getCause();
            if (cause instanceof ShiroException)
            {
                errorAttributes.put("message", cause.getMessage());
                errorAttributes.put("code", ((ShiroException) cause).getCode());
                log.error("登录异常：异常信息：[{}]", cause.getMessage());
            }
            else
            {
                errorAttributes.put("message", ResponseCode.SERVLET_EXCEPTION.message);
                errorAttributes.put("code", ResponseCode.SERVLET_EXCEPTION.code);
            }
        }
        else if (ex instanceof ValidationException)
        {
            errorAttributes.put("message", ResponseCode.VALIDATION_EXCEPTION.message);
            errorAttributes.put("code", ResponseCode.VALIDATION_EXCEPTION.code);
        }
        else
        {
            if (null == ex)
            {
                log.error("系统内部异常，请查看日志处理");
            }
            else
            {
                log.error("系统内部异常，请查看日志处理，异常信息：[{}]", ex.getMessage());
            }
            errorAttributes.put("message", ResponseCode.UNKNOWN_ERROR.message);
            errorAttributes.put("code", ResponseCode.UNKNOWN_ERROR.code);
        }
        return errorAttributes;
    }
}

