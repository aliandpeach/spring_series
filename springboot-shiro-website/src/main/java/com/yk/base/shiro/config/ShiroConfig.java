package com.yk.base.shiro.config;


import com.yk.base.shiro.filter.PasswordFilter;
import com.yk.base.shiro.filter.TokenFilter;
import com.yk.base.shiro.jwt.JwtTokenProvider;
import com.yk.base.shiro.matcher.PasswordMatcher;
import com.yk.base.shiro.matcher.TokenMatcher;
import com.yk.base.shiro.realm.PasswordRealm;
import com.yk.base.shiro.realm.TokenRealm;
import com.yk.base.shiro.token.CustomerToken;
import com.yk.base.shiro.token.PasswordToken;
import com.yk.user.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    private PasswordMatcher passwordMatcher;

    @Autowired
    private TokenMatcher tokenMatcher;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordRealm passwordRealm;

    @Autowired
    private TokenRealm tokenRealm;

    @Autowired
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") SecurityManager securityManager)
    {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
//        shiroFilterFactoryBean.setLoginUrl("/login");
//        shiroFilterFactoryBean.setSuccessUrl("");
//        shiroFilterFactoryBean.setSuccessUrl("");

        // 设置 登录和token认证的filter
        Map<String, Filter> signFilter = new LinkedHashMap<>();
        // 登录的拦截器
        signFilter.put("sign", new PasswordFilter(bcryptPasswordEncoder, jwtTokenProvider));
        // token的拦截器
        signFilter.put("token", new TokenFilter(jwtTokenProvider));
        shiroFilterFactoryBean.setFilters(signFilter);

        // 定义过滤器责任链, 控制哪些需要进行登录认证和Token认证
        Map<String, String> chainDefinition = new HashMap<>();
        // 需要登录的url
        chainDefinition.put("/signin", "sign");
        chainDefinition.put("/signup", "sign");
        chainDefinition.put("/modify/passwd", "sign");

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
    public SecurityManager defaultWebSecurityManager()
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
        passwordRealm.setCredentialsMatcher(passwordMatcher);
        passwordRealm.setAuthenticationTokenClass(PasswordToken.class);
        realmList.add(passwordRealm);
        // token校验器
        tokenRealm.setCredentialsMatcher(tokenMatcher);
        tokenRealm.setAuthenticationTokenClass(CustomerToken.class);
        realmList.add(tokenRealm);
        securityManager.setRealms(realmList);

        // doGetAuthorizationInfo 经过测试必须放在 securityManager.setRealms(realmList)后面, 如此在校验权限信息的时候, 就只用到tokenRealm, 不重复使用passwordRealm
        ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer(new ArrayList<>(Collections.singletonList(tokenRealm)));
        authorizer.setPermissionResolver(new WildcardPermissionResolver());
        securityManager.setAuthorizer(authorizer);

        // 无状态subjectFactory设置
        DefaultSessionStorageEvaluator evaluator
                = (DefaultSessionStorageEvaluator) ((DefaultSubjectDAO) securityManager.getSubjectDAO()).getSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(false);
        securityManager.setSubjectFactory(new DefaultWebSubjectFactory()
        {
            @Override
            public Subject createSubject(SubjectContext context)
            {
                context.setSessionCreationEnabled(false);
                return super.createSubject(context);
            }
        });

        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder(12);
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

    /*@Bean
    public DefaultAdvisorAutoProxyCreator proxyCreator()
    {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        *//*
         * setUsePrefix(false)用于解决一个奇怪的bug。在引入spring aop的情况下。
         * 在@Controller注解的类的方法中加入@RequiresRole注解，会导致该方法无法映射请求，导致返回404。
         * 加入这项配置能解决这个bug
         *//*
        advisorAutoProxyCreator.setUsePrefix(true);
        return advisorAutoProxyCreator;
    }*/

    /**
     * 开启aop注解支持
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager)
    {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
