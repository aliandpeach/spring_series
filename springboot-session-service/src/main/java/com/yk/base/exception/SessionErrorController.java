package com.yk.base.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
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
public class SessionErrorController implements ErrorController
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
                // 结果包含异常的message信息
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
        ServletWebRequest requestAttributes = new ServletWebRequest(request);
        Map<String, Object> attr = errorAttributes.getErrorAttributes(requestAttributes, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.EXCEPTION));
        return new ModelAndView("400");
    }

    /**
     * 除web页面外的错误处理，比如json/xml等
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public CustomException errorApiHandler(HttpServletRequest request)
    {
        ServletWebRequest requestAttributes = new ServletWebRequest(request);
        Throwable t = errorAttributes.getError(requestAttributes);
        Map<String, Object> attr = errorAttributes.getErrorAttributes(requestAttributes, ErrorAttributeOptions.defaults().including(ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.EXCEPTION));
        return new CustomException(String.valueOf(attr.getOrDefault("message", "")), HttpStatus.valueOf((int) attr.getOrDefault("status", 500)));
    }
}
