package com.yk.exception;


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
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.Map;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@Slf4j
@Controller
public class BlockchainErrorController implements ErrorController
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
    public ModelAndView page()
    {
        return buildPage(getRequest());
    }

    /**
     * 支持JSON的错误视图
     */
    @RequestMapping(value = ERROR_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public BaseResponse<?> body()
    {
        return assemblyBody(getRequest());
    }

    private ModelAndView buildPage(HttpServletRequest request)
    {
        Map<String, Object> errorAttributes = this.getErrorAttributes(request);
        Object httpStatus = RequestContextHolder.currentRequestAttributes().getAttribute("http_status", SCOPE_REQUEST);
        if (null != httpStatus)
        {
            return new ModelAndView("error/".concat(String.valueOf(httpStatus)), "errors", errorAttributes);
        }
        return new ModelAndView(
                "error/".concat(String.valueOf(errorAttributes.get("status"))), "errors", errorAttributes
        );
    }

    private BaseResponse<?> assemblyBody(HttpServletRequest request)
    {
        Map<String, Object> errorAttributes = this.getErrorAttributes(request);
        String message = String.valueOf(errorAttributes.get("message"));
        BaseResponse<?> baseResponse = new BaseResponse<>();
        baseResponse.setMessage(message);
        baseResponse.setCode((int) errorAttributes.get("code"));
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
                // 结果包含异常的message信息
                return super.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.EXCEPTION));
            }
        };
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request)
    {
        WebRequest requestAttributes = new ServletWebRequest(request);
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(requestAttributes,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.EXCEPTION));
        Throwable ex = this.errorAttributes.getError(requestAttributes);

        Object statusObject = request.getAttribute("javax.servlet.error.status_code");
        Object statusObject1 = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exceptionObject = request.getAttribute("javax.servlet.error.exception");

        errorAttributes.put("code", 1); // 这里理论上要和ControllerExceptionHandler里面保持一致的(按照不同的Exception给出不同的code)
        response.setStatus(null == errorAttributes.get("status") ? HttpStatus.BAD_REQUEST.value() : Integer.parseInt(errorAttributes.get("status").toString()));
        if (ex instanceof BlockchainException)
        {
            log.error("customer exception：[{}]", ex.getMessage());
            errorAttributes.put("message", ex.getMessage());
            errorAttributes.put("code", ((BlockchainException) ex).getCode());
            response.setStatus(HttpStatus.OK.value()); // 业务异常中, 返回200由请求者根据code去判断业务(或跳转或弹框)
        }
        else if (ex instanceof BindException)
        {
            BindException e = (BindException) ex;
            BindingResult bindingResult = e.getBindingResult();
            if (bindingResult.hasErrors())
            {
                FieldError fieldError = bindingResult.getFieldErrors().get(0);
                errorAttributes.put("message", StrBuilder.create(fieldError.getField(), fieldError.getDefaultMessage()).toString());
            }
        }
        else if (ex instanceof DataAccessException)
        {
            log.error("数据访问异常，异常信息：[{}]", ex.getMessage());
        }
        // api请求数据参数异常
        else if (ex instanceof HttpMessageNotReadableException)
        {
            log.error("请求数据参数异常，异常信息：[{}]", ex.getMessage());
        }
        else if (ex instanceof MethodArgumentNotValidException)
        {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            BindingResult bindingResult = e.getBindingResult();
            if (bindingResult.hasErrors())
            {
                if (null == bindingResult.getFieldError())
                {
                    ObjectError objectError = bindingResult.getAllErrors().get(0);
                    errorAttributes.put("message", objectError.getDefaultMessage());
                }
                else
                {
                    FieldError fieldError = bindingResult.getFieldErrors().get(0);
                    errorAttributes.put("message", fieldError.getDefaultMessage());
                }
                log.error(">>>异常：参数校验异常，异常信息：[{}]", e.getMessage());
            }
        }
        else if (ex instanceof HttpMediaTypeNotSupportedException || ex instanceof HttpMediaTypeNotAcceptableException)
        {
            log.error("媒体类型异常，异常信息：[{}]", ex.getMessage());
        }
        else if (ex instanceof ServletException)
        {
            ServletException e = (ServletException) ex;
            Throwable cause = e.getCause();
        }
        else if (ex instanceof ValidationException)
        {
            ValidationException e = (ValidationException) ex;

            errorAttributes.put("message", e.getCause() instanceof ValidateException ? e.getCause().getMessage() : ex.getMessage());
        }
        else
        {
            if (null == ex)
            {
                if (HttpStatus.NOT_FOUND.value() == (int) errorAttributes.get("status"))
                {
                    log.error("页面未找到，URL[{}]", errorAttributes.get("path"));
                }
                else
                {
                    log.error("系统内部异常，请查看日志处理");
                }
            }
            else
            {
                log.error("系统内部异常，请查看日志处理，异常信息：[{}]", ex.getMessage());
            }
        }
        return errorAttributes;
    }
}

