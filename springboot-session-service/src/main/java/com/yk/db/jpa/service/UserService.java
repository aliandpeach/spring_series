package com.yk.db.jpa.service;

import com.yk.base.exception.CustomException;
import com.yk.base.security.SessionProvider;
import com.yk.db.jpa.model.User;
import com.yk.db.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService
{
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final SessionProvider sessionProvider;

    @Autowired
    private final AuthenticationManager authenticationManager;

    public String signin(String username, String password)
    {
        try
        {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "登录成功";
        }
        catch (AuthenticationException e)
        {
            SecurityContextHolder.getContext().setAuthentication(null);
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signup(User user)
    {
        if (!userRepository.existsByName(user.getName()))
        {
            user.setPasswd(passwordEncoder.encode(user.getPasswd()));
            User u = userRepository.save(user);
            return "注册完成，请登录";
        }
        else
        {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public void delete(String username)
    {
        userRepository.deleteByName(username);
    }

    public User search(String username)
    {
        User appUser = userRepository.findByName(username);
        if (appUser == null)
        {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return appUser;
    }
}
