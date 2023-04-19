package com.yk.demo.upload;

import com.yk.exception.BlockchainException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class UploadFilter extends OncePerRequestFilter
{
    public static final String[] uploadList = new String[]{"/import/upload/multiple"};

    private static final Logger _logger = LoggerFactory.getLogger(UploadFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String uri = request.getRequestURI();
        String _contextPath = request.getServletContext().getContextPath();
        // SpringBoot的realPath是 DocumentRoot.java 中默认的 String[] COMMON_DOC_ROOTS = { "src/main/webapp", "public", "static" }; 前提是这三个目录文件存在
        // index.html 访问.log静态资源, 默认会在这三个目录读取
        String realPath = request.getServletContext().getRealPath("/");
        _logger.debug("realPath {}", realPath);
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        _logger.debug("contextPath {}", contextPath);
                /*if (Arrays.stream(uploadList).noneMatch(u -> uri.startsWith(contextPath + u)))
                {
                    filterChain.doFilter(request, response);
                    return;
                }*/
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        if (Arrays.stream(uploadList).noneMatch(u -> antPathMatcher.match(contextPath + u, contextPath + uri)))
        {
            filterChain.doFilter(request, response);
            return;
        }
        try
        {
            boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
            if (!isMultipartContent)
            {
                throw new BlockchainException(11, "");
            }
            // 提前在拦截器中对策略进行校验，不必等到spring的 DispatcherServlet 解析request获取完整的文件流之后才校验，否则大文件等待一段时间传上去才报告策略不存在，白浪费时间等待
            // getParts方法, 执行 Request.parseParts -> FileUploadBase.parseRequest 会解析request以及拷贝文件流, 所以大文件会等待很久
            // 这里的备注以前理解错了, getParts 会拷贝文件流的
                    /*
                    Collection<Part> list = request.getParts();
                    boolean is = list.stream().noneMatch(p -> p instanceof ApplicationPart);
                    if (is)
                    {
                        throw new BlockchainException(12, "");
                    }

                    String files = list.stream().map(t -> ((ApplicationPart) t).getName() + " : " + ((ApplicationPart) t).getSize()).collect(Collectors.joining("\n"));
                    _logger.debug("uri file upload {} info {}", uri, files);
                    */
            filterChain.doFilter(request, response);
        }
        catch (Exception e)
        {
            _logger.debug("uri file upload {} error", uri, e);
            throw e;
        }
    }
}
