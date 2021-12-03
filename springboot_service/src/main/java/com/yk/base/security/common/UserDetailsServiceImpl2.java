//package com.yk.base.security;
//
//import lombok.Data;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//@Service("userDetailsServiceImpl2")
//public class UserDetailsServiceImpl2 implements UserDetailsService
//{
//
//
//    /**
//     * Locates the user based on the username. In the actual implementation, the search
//     * may possibly be case sensitive, or case insensitive depending on how the
//     * implementation instance is configured. In this case, the <code>UserDetails</code>
//     * object that comes back may have a username that is of a different case than what
//     * was actually requested..
//     *
//     * @param username the username identifying the user whose data is required.
//     * @return a fully populated user record (never <code>null</code>)
//     * @throws UsernameNotFoundException if the user could not be found or the user has no
//     *                                   GrantedAuthority
//     */
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
//    {
//        UserDetails userDetails = null;
//        try
//        {
//            // 查询数据库用户和角色信息
//            TUser tUser = new TUser();
//            tUser.setUsername("admin");
//            tUser.setPasswd("Admin@123");
//            Collection<GrantedAuthority> authList = getAuthorities(tUser.getUsername());
//            userDetails = new User(username, tUser.getPasswd().toLowerCase(), true, true, true, true, authList);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return userDetails;
//    }
//
//    /**
//     * 根据用户名获取角色
//     */
//    private Collection<GrantedAuthority> getAuthorities(String username)
//    {
//        List<GrantedAuthority> authList = new ArrayList<>();
//        authList.add(new SimpleGrantedAuthority("ROLE_USER"));
//        authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        return authList;
//    }
//
//    @Data
//    private static class TUser
//    {
//        private String username;
//        private String passwd;
//    }
//}
