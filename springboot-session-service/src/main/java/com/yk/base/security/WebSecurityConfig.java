package com.yk.base.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Configuration
@EnableWebSecurity
/**
 * prePostEnabled
 *  解锁 @PreAuthorize 和 @PostAuthorize 两个注解。
 *  从名字就可以看出@PreAuthorize 注解会在方法执行
 *  前进行验证，而 @PostAuthorize 注解会在方法执行后进行验证。
 *
 *  securedEnabled
 *   解锁@Secured注解是用来定义业务方法的安全配置。
 *   在需要安全[角色/权限等]的方法上指定 @Secured，
 *   并且只有那些角色/权限的用户才可以调用该方法
 * jsr250E
 *  jsr250Enabled 为 true ，就开启了 JavaEE 安全注解中的以下三个：
 *  1.@DenyAll： 拒绝所有访问
 *  2.@RolesAllowed({“USER”, “ADMIN”})： 该方法只要具有"USER", "
 *  ADMIN"任意一种权限就可以访问。这里可以省略前缀ROLE_，
 *  实际的权限可能是ROLE_ADMIN
 *  3.@PermitAll： 允许所有访问
 *
 *
 *
 *  一丶
 *  1. 在session场景中, SecurityContextPersistenceFilter 负责从session中获取请求用户信息, loadContext， 放入SecurityContext, 且负责更新session信息
 *  若用户未登录, SecurityContext.authentication为null
 *  PasswordAuthenticationFilter 在 SecurityContextPersistenceFilter之后,  负责登录校验, 校验完璧需要手动放入 setAuthentication,
 *  后续由SessionManagementFilter管理session的策略(例如用户最多登录一个, 之前的session会被挤掉等)
 *  最后忧SecurityContextPersistenceFilter.HttpSessionSecurityContextRepository.SaveToSessionResponseWrapper保存session到SecurityContext
 *  2. 请求携带已登录cookie， 执行的顺序依旧和上面一致, 只是基于判断不再执行PasswordAuthenticationFilter相关逻辑, 如果所携带cookie已经失效, 或未携带cookie
 *  执行到 AnonymousAuthenticationFilter中, 会setAuthentication放入 匿名信息, 最后由FilterSecurityInterceptor中执行校验后抛出AccessDinedException
 *
 *  二丶
 *  1. jwt场景中需要disable掉sessionManagement(SessionManagementFilter)和 securityContext （SecurityContextPersistenceFilter） 保证session不会被创建
 *  2. PasswordAuthenticationFilter 负责登录校验, 校验完璧需要手动放入 setAuthentication, 在后续getAuthentication使用用户名和权限信息生成jwt token响应到客户端
 *  3. 校验jwt如果过期或者为空, 则继续doFilter执行后续的拦截器, 执行到 AnonymousAuthenticationFilter中, 会setAuthentication放入 匿名信息,
 *     最后由FilterSecurityInterceptor中执行校验后抛出AccessDinedException, 进而交给ExceptionTranslationFilter去handle,
 *     也可以自行抛出异常去处理.
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)// @PreAuthorize生效
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private SessionProvider sessionProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * HttpSecurity的构建目标仅仅是FilterChainProxy中的一个SecurityFilterChain
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        /**
         * anyRequest          |   匹配所有请求路径
         * access              |   SpringEl表达式结果为true时可以访问
         * anonymous           |   匿名可以访问
         * denyAll             |   用户不能访问
         * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
         * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
         * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
         * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
         * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
         * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
         * permitAll           |   用户可以任意访问
         * rememberMe          |   允许通过remember-me登录的用户访问
         * authenticated       |   用户登录后可访问
         */
        String[] excepts = Optional.ofNullable(sessionProvider.getExcepts()).orElse("").split(";");
        http.authorizeRequests()
                // FilterSecurityInterceptor -> AbstractSecurityInterceptor.beforeInvocation -> accessDecisionManager.decide (Matchers配置在这里使用到)
                .antMatchers(excepts).permitAll()
                .antMatchers("/h2-console/**/**").permitAll()
                // /api/vip/**匹配的所有请求都要有ROLE_VIP角色
//                .antMatchers("/api/vip/**").hasRole("VIP")
                .anyRequest().authenticated();  // 其余请求都需要过滤

        // formLogin -> new FormLoginConfigurer 注释formLogin拦截器UsernamePasswordAuthenticationFilter就不会加入filter chain链中
         http.formLogin().loginPage("/login").disable();
        // CookieCsrfTokenRepository 策略会把后台生成的csrf组装成cookie响应到前端, 前端获取后放入http header, header name必须是X-XSRF-TOKEN或者放入getParameter, name 必须是_csrf
        // HttpSessionCsrfTokenRepository 则是吧生成的csrf放入session
        http.csrf().csrfTokenRepository(new CookieCsrfTokenRepository())
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/signin"))
                .ignoringAntMatchers("/api/signup")
                // csrf校验失败跳转至RestAccessDeniedHandler异常处理
                // requireCsrfProtectionMatcher默认DefaultRequiresCsrfMatcher放行 "GET", "HEAD", "TRACE", "OPTIONS"
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/api/**"))
                .disable(); // 测试的时候disable
        http.sessionManagement().disable(); // 禁用session

        http.cors(); // 支持跨域
        // 添加header设置，支持跨域和ajax请求
        http.headers().addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
                new Header("Access-control-Allow-Origin", "*"),
                new Header("Access-Control-Expose-Headers", "Authorization"))));

        // session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http.sessionManagement()
                /*.invalidSessionStrategy(new InvalidSessionStrategy()
                {
                    @Override
                    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException
                    {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().write("认证信息无效，请重新登录！");
                    }
                })*/
                // Session的并发控制,这里设为最多一个，只允许一个用户登录,如果同一个账户两次登录,那么第一个账户将被踢下线
                .maximumSessions(1)
                // session过期处理策略
                .expiredSessionStrategy(new SessionInformationExpiredStrategy()
                {
                    /**
                     * session过期处理策略
                     */
                    @Override
                    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException
                    {
                        HttpServletResponse response = event.getResponse();
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().write("认证信息过期，请重新登录！");
                    }
                });
                // 当同时登陆的最大session 数达到 maximumSessions 配置后，拒绝后续同账户登录，
                // 抛出信息 ：Maximum sessions of 1 for this principal exceeded
                /*.maxSessionsPreventsLogin(true)
                .and()
                .sessionAuthenticationFailureHandler(new AuthenticationFailureHandler()
                {
                    // Maximum sessions of 1 for this principal exceeded
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException
                    {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().write("账号认证失败");
                    }
                });*/

        // If a user try to access a resource without having enough permissions
        http.exceptionHandling().accessDeniedPage("/login");

        // 如果用户处于未登录（anonymous）状态，会先触发AuthenticationEntryPoint，如果没有配置，则会重定向至登录页
        // 如果用户处于登陆（authenticated）状态，会触发AccessDeniedHandler
        http.exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint());
        http.exceptionHandling().accessDeniedHandler(new RestAccessDeniedHandler());

        http.apply(new SessionFilterConfigurer(sessionProvider, authenticationManager));

        // Optional, if you want to test the API from a browser
        // http.httpBasic();
    }

    /**
     * WebSecurity构建目标是整个Spring Security安全过滤器FilterChainProxy,
     */
    @Override
    public void configure(WebSecurity web) throws Exception
    {
        // Allow swagger to be accessed without authentication
        web.ignoring().antMatchers("/v2/api-docs")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/configuration/**")
                .antMatchers("/webjars/**")
                .antMatchers("/public")
                .antMatchers("/static")
                .antMatchers("/describe.html")
                .antMatchers("/favicon.ico")

                // Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
                .and()
                .ignoring()
                .antMatchers("/h2-console/**/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(sessionProvider.getUserDetailsService());
    }
}
