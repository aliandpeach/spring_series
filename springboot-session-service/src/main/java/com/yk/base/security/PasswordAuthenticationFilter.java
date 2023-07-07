package com.yk.base.security;

import com.yk.base.filter.NewHttpServletRequestWrapper;
import com.yk.db.jpa.dto.UserDataDTO;
import com.yk.httprequest.JSONUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 密码校验拦截器 只在登录或者修改密码的接口中进行拦截
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/18 19:10:29
 */
public class PasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordAuthenticationFilter.class);

    private final ThreadLocal<HttpServletRequestWrapper> threadLocal = new ThreadLocal<>();

    public PasswordAuthenticationFilter(AuthenticationManager authenticationManager, SessionAuthenticationStrategy strategy)
    {
        super.setAuthenticationManager(authenticationManager);
        super.setContinueChainBeforeSuccessfulAuthentication(false);
        super.setFilterProcessesUrl("/api/signin");
        // authenticationManager().authenticate -> onAuthentication -> registerNewSession
        // 这里不要自己new对象, strategy是基于http.sessionManagement().maximumSessions配置内部生成的, 生成的strategy放入http.setSharedObject()
        // super.setSessionAuthenticationStrategy(new ConcurrentSessionControlAuthenticationStrategy(new SessionRegistryImpl()));
//        if (null != strategy)
//            super.setSessionAuthenticationStrategy(strategy);

        // ==========================
        // onAuthenticationSuccess应该执行chain.doFilter让密码校验的拦截器继续往下流转, 但是这里没有传入chain, 所以不再使用默认的SavedRequestAwareAuthenticationSuccessHandler
        /*super.setAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler()
        {

            // SavedRequestAwareAuthenticationSuccessHandler这个类记住了你上一次的请求路径，比如：
            // 你请求user.html。然后被拦截到了登录页，这时候你输入完用户名密码点击登录，会自动跳转到user.html，而不是主页面。
            // 若是前后分离项目则实现接口即可

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException
            {
                LOGGER.info("登录成功");
                if (true)
                {
                    // 这里应该执行chain.doFilter让密码校验的拦截器继续往下流转, 但是这里没有传入chain, 所以不再使用默认的SavedRequestAwareAuthenticationSuccessHandler
                }
                else
                {
                    // 会帮我们跳转到上一次请求的页面上
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            }
        });*/
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request,
                                             HttpServletResponse response)
    {
        return new AntPathRequestMatcher("/api/signin").matches(request);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException
    {
        NewHttpServletRequestWrapper httpServletRequestWrapper;
        try
        {
            httpServletRequestWrapper = new NewHttpServletRequestWrapper(request);
        }
        catch (IOException e)
        {
            return super.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken("", ""));
        }
        threadLocal.set(httpServletRequestWrapper);
        UserDataDTO user = null;
        try (InputStream input = httpServletRequestWrapper.getInputStream())
        {
            String json = IOUtils.toString(input, StandardCharsets.UTF_8.name());
            user = JSONUtil.fromJson(json, UserDataDTO.class);
            LOGGER.info("request user info success");
        }
        catch (IOException e)
        {
            LOGGER.error("get request body error {}", e.getMessage());
            user = new UserDataDTO();
        }
        String name = Optional.ofNullable(user.getName()).orElse("");
        String pwd = Optional.ofNullable(user.getPasswd()).orElse("");
        // authenticate调用链: WebSecurityConfigurerAdapter.authenticate -> ProviderManager.authenticate -> AbstractUserDetailsAuthenticationProvider.authenticate
        //                     -> retrieveUser(查询用户以及权限) -> createSuccessAuthentication(权限信息被放入) -> setAuthenticated(true)
        //                     -> successfulAuthentication -> SavedRequestAwareAuthenticationSuccessHandler.onAuthenticationSuccess
        //                     -> SimpleUrlAuthenticationSuccessHandler.onAuthenticationSuccess -> handle -> sendRedirect
        //                     -> ... -> HttpSessionSecurityContextRepository.saveContext(保存context到session)

        // 1. 如果不执行super.successfulAuthentication, 则需要再doFilter之前, 执行SecurityContextHolder.getContext().setAuthentication(authResult) (之后context保存至session)
        //    则第一次登录后才会执行到 SessionManagementFilter -> sessionAuthenticationStrategy.onAuthentication, 其他已登录的接口调用都执行不进来（因为context已经保存到了session)
        // 2. 更新用户的角色信息, 需要如何更新session中的context
        // 3. session保存到redis -> spring-session-data-redis -> RedisIndexedSessionRepository

        // 此处返回的Authentication按照上面的调用链, 已经放入了权限信息
        return super.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(name, pwd));
        // 调用接口, 第一个执行的拦截器是 SecurityContextPersistenceFilter 用于获取已经登录保存到session的context
        // SecurityContextPersistenceFilter -> HttpSessionSecurityContextRepository.loadContext
        // -> SecurityContext context = httpSession.getAttribute("SPRING_SECURITY_CONTEXT")
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException
    {
        // 用户以及权限信息放入session
//        request.getSession().setAttribute(SessionProvider.SESSION_USER_KEY, authResult);
        // 不使用doFilter流转, 直接调用super方法执行setAuthentication(authResult)
//        super.successfulAuthentication(threadLocal.get(), response, chain, authResult);

        // 解开这里的注释, 则注释掉上面一行super.successfulAuthentication,
        // 配置了maximumSessions的情况下, super.setSessionAuthenticationStrategy也要注释(否则 ConcurrentSessionControlAuthenticationStrategy 执行两次),
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(threadLocal.get(), response);
    }

    /**
     * 1. 通过验证 spring-security中抛出的异常, 最后不会跳转至ErrorController, 因此在每个可能的异常点进行日志记录外后, 直接使用response 给前端调用者返回数据
     *
     * 2. 或者通过 .getRequestDispatcher("/error").forward 跳转至ErrorController, 但需要设置 message exception status
     *
     * ①response.sendRedirect(url)-----重定向到指定URL
     * request.getRequestDispatcher(url).forward(request,response) -----请求转发到指定URL
     *
     * ②response.sendRedirect(url)-----是客户端跳转
     * request.getRequestDispatcher(url).forward(request,response) -----是服务器端跳转
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException
    {
//        response.setContentType("application/json");
//        String result = JSONUtil.toJson(new CustomException("用户名或者密码错误", HttpStatus.FORBIDDEN));
//        response.getWriter().write(result == null ? "{\"message\": \"用户名或者密码错误\"}" : result);

        /*request.setAttribute("javax.servlet.error.status_code", HttpStatus.FORBIDDEN.value());
        request.setAttribute("javax.servlet.error.exception", failed);
        request.setAttribute("javax.servlet.error.message", "用户名或者密码错误");
        request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
        request.getRequestDispatcher("/error").forward(request,response);*/

        // 不使用上面的自定义跳转异常, 也可以直接调用super方法进入RestAuthenticationEntryPoint
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
