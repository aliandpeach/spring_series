package com.yk.base.security;

import com.yk.base.exception.CustomException;
import com.yk.base.exception.ResponseCode;
import com.yk.db.jpa.model.User;
import com.yk.db.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/17 11:40:57
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService
{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        final User user = userRepository.findByName(username);

        if (user == null)
        {
            throw new CustomException(ResponseCode.ACCOUNT_USER_NOT_EXIST_ERROR.message, ResponseCode.ACCOUNT_USER_NOT_EXIST_ERROR.code);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPasswd())
                .roles() // 设置不带ROLE_开头的角色名称数组
                .authorities(user.getRoles()) // 设置菜单权限名数据(后续增加角色菜单权限关系)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
