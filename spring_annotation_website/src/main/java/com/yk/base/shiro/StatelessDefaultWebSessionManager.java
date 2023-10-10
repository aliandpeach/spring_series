package com.yk.base.shiro;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * 重写Web管理, 不再使用cookie, 前端传输必须在header中放入 Authorization: Bearer xxxx
 *
 * @author yangk
 * @version 1.0
 * @since 2023/09/26 17:50:00
 */
public class StatelessDefaultWebSessionManager extends DefaultWebSessionManager
{
    /**
     * 这个是客户端请求给服务端带的header
     */
    public final static String HEADER_TOKEN_NAME = "Authorization";
    public final static Logger log = LoggerFactory.getLogger(StatelessDefaultWebSessionManager.class);
    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    /**
     * 重写getSessionId,分析请求头中的指定参数，做用户凭证sessionId
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response)
    {
        String sessionId = WebUtils.toHttp(request).getHeader(HEADER_TOKEN_NAME);
        if (StringUtils.isEmpty(sessionId))
        {
            return super.getSessionId(request, response);
        }
        //如果请求头中有 memberToken 则其值为sessionId
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
        if (sessionId.startsWith("Bearer "))
        {
            return sessionId.substring(7);
        }
        return sessionId;
    }
}
