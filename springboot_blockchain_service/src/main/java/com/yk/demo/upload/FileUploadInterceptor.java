package com.yk.demo.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class FileUploadInterceptor implements HandlerInterceptor
{
    private static final Logger _logger = LoggerFactory.getLogger(FileUploadInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        /*String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (Arrays.stream(uploadList).noneMatch(u -> uri.startsWith(contextPath + u)))
        {
            return true;
        }
        if (request instanceof MultipartRequest)
        {
            MultipartRequest multipartRequest = (MultipartRequest) request;
            long totalFileSize = 0;

            Iterator<String> fileNames = multipartRequest.getFileNames();
            while (fileNames.hasNext())
            {
                String fileName = fileNames.next();
                MultipartFile file = multipartRequest.getFile(fileName);
                if (file != null && !file.isEmpty())
                {
                    totalFileSize += file.getSize();
                }
            }
            _logger.debug("uri file upload {} totalFileSize {}", uri, totalFileSize);
        }*/
        return true;
    }
}
