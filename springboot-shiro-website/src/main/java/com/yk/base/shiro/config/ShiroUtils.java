package com.yk.base.shiro.config;

import com.yk.base.exception.ShiroException;
import com.yk.base.shiro.realm.TokenRealm;
import com.yk.login.controller.SignController;
import com.yk.user.model.User;
import com.yk.user.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShiroUtils
{
    @Autowired
    private UserService userService;

    @Autowired
    private TokenRealm tokenRealm;

    /**
     * 获取当前用户Session
     */
    public static Session getSession()
    {
        return SecurityUtils.getSubject().getSession();
    }

    /**
     * 用户登出
     */
    public static void logout()
    {
        SecurityUtils.getSubject().logout();
    }

    /**
     * 获取当前用户信息(结果是User对象还是username字符串取决于realm.doGetAuthenticationInfo返回的AuthenticationInfo放入的是什么, 该工程中放入的是username)
     */
    public static Object getUsername()
    {
        return SecurityUtils.getSubject().getPrincipal();
    }


    /**
     * 更新自定义的过滤器责任链, 用于控制哪些需要进行登录认证和Token认证
     */
    public void updateChainDefinition(ShiroFilterFactoryBean shiroFilterFactoryBean)
    {
        synchronized (SignController.class)
        {
            AbstractShiroFilter shiroFilter;
            try
            {
                shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
            }
            catch (Exception e)
            {
                throw new ShiroException("get ShiroFilter from shiroFilterFactoryBean error!");
            }
            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter.getFilterChainResolver();
            DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();

            // 清空拦截管理器中的存储
            manager.getFilterChains().clear();
            // 清空拦截工厂中的存储,如果不清空这里,还会把之前的带进去
            // 如果仅仅是更新的话,可以根据这里的 map 遍历数据修改,重新整理好权限再一起添加
            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
            // 动态查询数据库中所有权限
            Map<String, String> chainDefinition = loadChainDefinition();
            shiroFilterFactoryBean.setFilterChainDefinitionMap(chainDefinition);
            // 重新构建生成拦截
            Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();
            for (Map.Entry<String, String> entry : chains.entrySet())
            {
                manager.createChain(entry.getKey(), entry.getValue());
            }
        }
    }

    private Map<String, String> loadChainDefinition()
    {
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
        return chainDefinition;
    }

    public void updateUserPermission()
    {
        // 查询当前角色的用户shiro缓存信息 -> 实现动态权限
        List<User> userList = userService.queryAllList();
        if (CollectionUtils.isEmpty(userList))
        {
            return;
        }
        for (User user : userList)
        {
            tokenRealm.reloadAuthorizationInfo(user.getUsername());
        }
    }
}
