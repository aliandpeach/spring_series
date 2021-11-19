package com.yk.db.jpa.service;

import com.yk.base.exception.CustomException;
import com.yk.base.security.JwtTokenProvider;
import com.yk.db.jpa.model.User;
import com.yk.db.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class UserService
{
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private final AuthenticationManager authenticationManager;

    public String signin(String username, String password)
    {
        try
        {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, userRepository.findByName(username).getRoles());
        }
        catch (AuthenticationException e)
        {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signup(User user)
    {
        if (!userRepository.existsByName(user.getName()))
        {
            user.setPasswd(passwordEncoder.encode(user.getPasswd()));
            User u = userRepository.save(user);
            return jwtTokenProvider.createToken(user.getName(), user.getRoles());
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

    public User whoami(HttpServletRequest req)
    {
        return userRepository.findByName(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }

    public String refresh(String username)
    {
        return jwtTokenProvider.createToken(username, userRepository.findByName(username).getRoles());
    }
}
