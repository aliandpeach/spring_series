package com.yk.base.x509;

import com.yk.base.exception.GlobalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CheckClientTrustedFilter extends OncePerRequestFilter
{
    private static final Logger logger = LoggerFactory.getLogger(CheckClientTrustedFilter.class);

    private List<X509TrustManager> x509TrustManagerList = new ArrayList<>();

    private HashSet<String> listOfVerifyUri = new HashSet<>();

    public CheckClientTrustedFilter(TrustManager[] trustManagers, String verifyUri)
    {
        Optional.ofNullable(verifyUri).ifPresent(u -> this.listOfVerifyUri.addAll(Arrays.stream(u.split(",")).collect(Collectors.toList())));
        if (null == trustManagers)
        {
            return;
        }
        for (TrustManager trustManager : trustManagers)
        {
            if (trustManager instanceof X509TrustManager)
            {
                this.x509TrustManagerList.add((X509TrustManager) trustManager);
            }
        }
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String uri = request.getRequestURI();
        boolean except = isExcept(uri, listOfVerifyUri, getServletContext().getContextPath());
        if (!except)
        {
            filterChain.doFilter(request, response);
            return;
        }
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        if (this.x509TrustManagerList.size() == 0)
        {
            logger.error("证书链校验失败, 没有加载本地证书库, {}, {}", uri, listOfVerifyUri);
            response.setStatus(500);
            throw new GlobalException(500, "证书链校验失败, 没有加载本地证书库");
        }
        if (null == certs)
        {
            logger.error("客户端证书链校验失败, 客户端证书不存在, {}, {}", uri, listOfVerifyUri);
            response.setStatus(500);
            throw new GlobalException(500, "客户端证书链校验失败, 证书链不存在");
        }
        /*for (X509Certificate client : certs)
        {
            try
            {
                // 如果签发的客户端证书链为三层或三层以上, 那就只能逐级进行签名校验了 (rootCA 校验中间证书, 中间证书再校验下一级...)
                client.verify(root.getPublicKey());
            }
            catch (Exception e)
            {
                logger.error("客户端证书链校验失败, 公钥签名验证失败", e);
                response.setStatus(500);
                throw new GlobalException(500, "客户端证书链校验失败, 公钥签名验证失败");
            }
        }*/
        for (X509TrustManager manager : x509TrustManagerList)
        {
            try
            {
                manager.checkServerTrusted(certs, "RSA");
            }
            catch (Exception e)
            {
                logger.error("客户端证书链校验失败", e);
                response.setStatus(500);
                throw new GlobalException(500, "客户端证书链校验失败");
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void initFilterBean()
    {

    }

    public boolean isExcept(String requestURL, Set<String> exclusionURLs, String contextPath)
    {
        if (requestURL.startsWith(contextPath))
        {
            requestURL = requestURL.substring(requestURL.indexOf(requestURL) + 1);
        }
        AntPathMatcher matcher = new AntPathMatcher(File.separator);
        boolean result = false;
        for (String match : exclusionURLs)
        {
            if ((result = matcher.match(match, requestURL)))
            {
                break;
            }
        }
        return result;
    }
}
