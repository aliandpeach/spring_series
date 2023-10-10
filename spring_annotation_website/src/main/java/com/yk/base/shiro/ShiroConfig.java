package com.yk.base.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ShiroConfig
{

    @Autowired
    private UsernamePasswordRealm usernamePasswordRealm;

    @Autowired
    private UsernamePasswordMatcher usernamePasswordMatcher;

    /**
     *   <filter>
     *     <filter-name>shiroFilter</filter-name>
     *     <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
     *   </filter>
     *   <filter-mapping>
     *     <filter-name>shiroFilter</filter-name>
     *     <url-pattern>/*</url-pattern>
     *     <dispatcher>REQUEST</dispatcher>
     *     <dispatcher>FORWARD</dispatcher>
     *   </filter-mapping>
     *
     * shiroFilter 作为bean用于通过servletContext.addFilter注册生成拦截器 (mvc 没有FilterRegistrationBean, 而是通过DelegatingFilterProxy)
     * MyWebAppInitializer -> servletContext.addFilter("shiroFilter", new DelegatingFilterProxy())
     *
     * ShiroFilter拦截器生效, getSession()才会生效, 而不会返回null
     *
     * @param securityManager securityManager
     * @return ShiroFilter
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") WebSecurityManager securityManager)
    {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 设置 登录和token认证的filter
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("token", new SessionFilter());
        shiroFilterFactoryBean.setFilters(filters);

        shiroFilterFactoryBean.setLoginUrl("/user/signin");

        // 定义过滤器责任链, 控制哪些需要进行登录认证和Token认证
        Map<String, String> chainDefinition = new HashMap<>();
        // 需要登录的url
//        chainDefinition.put("/user/signin", "sign");
//        chainDefinition.put("/user/signup", "sign");
//        chainDefinition.put("/user/modify/passwd", "sign");

        // 除了忽略的和登录的url, 其余都要进行token验证
        chainDefinition.put("/**", "token");

        // 忽略的url
        chainDefinition.put("/login", "anon");
        chainDefinition.put("/register", "anon");
        // 批量忽略的url, 注意是两个星号
        chainDefinition.put("/api/**", "anon");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(chainDefinition);
        return shiroFilterFactoryBean;
    }

    @Bean(name = "securityManager")
    public WebSecurityManager defaultWebSecurityManager(SessionManager sessionManager)
    {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        // doGetAuthenticationInfo
        securityManager.setAuthenticator(new ModularRealmAuthenticator()
        {
            @Override
            public AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException
            {
                assertRealmsConfigured();
                Collection<Realm> realms = getRealms().stream()
                        .filter(realm -> realm.supports(authenticationToken)).collect(Collectors.toList());

                // 如果执行 doMultiRealmAuthentication 逻辑, 则PasswordRealm中抛出的自定义异常无法在PasswordFilter被识别到
                // 因为 doMultiRealmAuthentication中做了多个realm的处理, 没有直接抛出自定义异常
                // 而 doSingleRealmAuthentication直接抛出了自定义异常, 该自定义异常又被放到了AuthenticationException中, 最后通过e.getCause()得到该自定义异常
                if (realms.size() == 1)
                {
                    return doSingleRealmAuthentication(realms.iterator().next(), authenticationToken);
                }
                else
                {
                    return doMultiRealmAuthentication(realms, authenticationToken);
                }
            }
        });

        List<Realm> realmList = new LinkedList<>();
        // 密码校验器
        usernamePasswordRealm.setCredentialsMatcher(usernamePasswordMatcher);
        usernamePasswordRealm.setAuthenticationTokenClass(UsernamePasswordToken.class);
        realmList.add(usernamePasswordRealm);
        securityManager.setRealms(realmList);

        // doGetAuthorizationInfo 经过测试必须放在 securityManager.setRealms(realmList)后面, 如此在校验权限信息的时候, 就只用到tokenRealm, 不重复使用passwordRealm
        /*ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer(new ArrayList<>(Collections.singletonList(tokenRealm)));
        authorizer.setPermissionResolver(new WildcardPermissionResolver());
        securityManager.setAuthorizer(authorizer);*/

        // 无状态subjectFactory设置
        DefaultSessionStorageEvaluator evaluator
                = (DefaultSessionStorageEvaluator) ((DefaultSubjectDAO) securityManager.getSubjectDAO()).getSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(true);
        securityManager.setSubjectFactory(new DefaultWebSubjectFactory()
        {
            @Override
            public Subject createSubject(SubjectContext context)
            {
                context.setSessionCreationEnabled(true);
                return super.createSubject(context);
            }
        });

        // SessionManager不使用自定义, 默认为 ServletContainerSessionManager,
        // 前端请求被 AbstractShiroFilter -> doFilterInternal -> createSubject -> resolveSession -> getSession 解析为Session,
        // (获取HttpSession组装为Session, 组装Subject信息), 通过UserFilter, 从Subject获取当前登录用户, 若不存在则redirectToLogin

        securityManager.setSessionManager(sessionManager);
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;

        // 若使用 ShiroWebConfiguration 作为bean去配置shiro.userNativeSessionManager=true, 通过nativeSessionManager方法去创建SessionManager,
        // 内部的SessionDAO等bean都需要配置, 否则使用默认的
    }

    /**
     * <bean id="sessionIdCookie" class="org.apache.shiro.web.servlet.SimpleCookie">
     * 		<constructor-arg value="Authorization" />
     * 		<property name="httpOnly" value="true" />
     * </bean>
     */

    @Bean
    public SimpleCookie simpleCookie(SessionDAO sessionDAO)
    {
        SimpleCookie simpleCookie = new SimpleCookie("Authorization");
        simpleCookie.setHttpOnly(true);
        return simpleCookie;
    }

    /**
     * 自定义sessionManager
     */
    @Bean
    public SessionManager sessionManager(SessionDAO sessionDAO, SimpleCookie simpleCookie)
    {
        // default JSESSIONID (native session), Cookie: JSESSIONID=665069e5066exxx
        // 使用setSessionIdCookie设置后, 改为Authorization, 变为 Cookie: Authorization=665069e5066exxx
        // 若不想使用Cookie作为header头, 而是Authorization: Bearer 665069e5066exxx, 则需要重写 DefaultWebSessionManager部分方法去解析Authorization作为header头
//        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        StatelessDefaultWebSessionManager sessionManager = new StatelessDefaultWebSessionManager();
        // 不再使用cookie
        sessionManager.setSessionIdCookieEnabled(false);
        sessionManager.setSessionDAO(sessionDAO);
        // session超时时间
        sessionManager.setGlobalSessionTimeout(1800000000);
        return sessionManager;
    }

    /**
     * redis缓存， 如有需要可自行实现 SessionDAO, CacheManager, Cache<K, V>
     */
    @Bean
    public SessionDAO sessionDAO(RedisManager redisManager)
    {
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setRedisManager(redisManager);
        return sessionDAO;
    }

    /**
     * shiro 获取cache的调用链： ( 这里的 AbstractNativeSessionManager 就是 DefaultWebSessionManager )
     * AbstractShiroFilter -> doFilterInternal -> createSubject -> resolveSession -> resolveContextSession -> AbstractNativeSessionManager.getSession ->
     *  lookupSession -> retrieveSession -> retrieveSessionFromDataSource -> sessionDAO.readSession -> getCachedSession
     *
     *  若是native session则从 getSession方法开始进入 ServletContainerSessionManager.getSession -> httpServletRequest.getSession
     *
     * AbstractShiroFilter -> updateSessionLastAccessTime 用户更cache的最后一次访问时间
     * session.touch() 根据org.apache.shiro.session.Session 的向下类型, 分别进入org.apache.shiro.web.session.HttpServletSession.touch()
     *                  或者 DelegatingSession.touch, 其中 DelegatingSession是在 lookupSession 生成对象之后包装为 DelegatingSession
     *
     */
    @Bean
    public CacheManager cacheManager(RedisManager redisManager)
    {
        // 若自定义则实现AbstractCacheManager 以及实现Cache<K,V>接口
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        // 这里的redisManager 放入Cache实现类RedisCache中, 在服务初始化的时候, 通过getCache获取RedisCache, 进而从redis获取数据
        redisCacheManager.setRedisManager(redisManager);
        return redisCacheManager;
    }

    @Bean
    public RedisManager redisManager()
    {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost("127.0.0.1");
        redisManager.setPort(6379);
        return redisManager;
    }

    /**
     * ehcache2.6以后, vm中只允许存在一个实例net.sf.ehcache.CacheManager实例
     * 通过EhCacheManagerFactoryBean设置shared=true避免多个实例
     */
    /*@Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean()
    {
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setShared(true);
        return factoryBean;
    }

    @Bean
    public CacheManager cacheManager(EhCacheManagerFactoryBean ehCacheManagerFactoryBean)
    {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManager(ehCacheManagerFactoryBean.getObject());
        ehCacheManager.setCacheManagerConfigFile("classpath:shiro-ehcache.xml");
        return ehCacheManager;
    }*/

    @Bean
    public SessionDAO sessionDAOEh(CacheManager cacheManagerEh)
    {
        EnterpriseCacheSessionDAO enterpriseCacheSessionDAO = new EnterpriseCacheSessionDAO();
        //设置session缓存的名字 默认为 shiro-activeSessionCache
        enterpriseCacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
        enterpriseCacheSessionDAO.setCacheManager(cacheManagerEh);
        return enterpriseCacheSessionDAO;
    }

    /**
     * 开始是由于spring被初始化两遍,导致CacheManager初始化两次(最终有一个bean), 内部的net.sf.ehcache.CacheManager 被new了两遍
     */
    @Bean
    public CacheManager cacheManagerEh()
    {
        EhCacheManager ehCacheManager = new EhCacheManager();
        // 通过getCache组装EhCache, 在服务初始化的时候, 在需要sessionDAO操作缓存的时候, 通过 CacheManager -> getCache获取EhCache, 进而获取数据
        ehCacheManager.setCacheManagerConfigFile("classpath:shiro-ehcache.xml");
        return ehCacheManager;
    }

    /**
     * Shiro生命周期处理器
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor()
    {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro注解(如@RequiresRoles,@RequiresPermissions),
     * 需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator()
    {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        /*
         * setUsePrefix(false)用于解决一个奇怪的bug。在引入spring aop的情况下。
         * 在@Controller注解的类的方法中加入@RequiresRole注解，会导致该方法无法映射请求，导致返回404。
         * 加入这项配置能解决这个bug
         */
//        advisorAutoProxyCreator.setUsePrefix(true);
        // 开启shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助Spring-AOP扫描使用shiro注解的类,并在必要时进行安全逻辑验证
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 开启aop注解支持
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(WebSecurityManager securityManager)
    {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
