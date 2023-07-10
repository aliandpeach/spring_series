package com.yk.db.jpa.service;

import com.yk.base.exception.CustomException;
import com.yk.base.exception.ResponseCode;
import com.yk.base.security.JwtTokenProvider;
import com.yk.db.jpa.model.Role;
import com.yk.db.jpa.model.User;
import com.yk.db.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<String, String> signin(String username, String password)
    {
        try
        {
            // JwtAuthenticationFilter已经做过校验
//            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated())
            {
                org.springframework.security.core.userdetails.User user =
                        (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                String access_token = jwtTokenProvider.createToken(user.getUsername(), authentication.getAuthorities().stream().map(t -> new SimpleGrantedAuthority(t.getAuthority())).collect(Collectors.toList()));
                return Collections.singletonMap("access_token", access_token);
            }
            throw new CustomException("Invalid username/password supplied", ResponseCode.SIGN_IN_ERROR.code);
        }
        catch (Exception e)
        {
            SecurityContextHolder.getContext().setAuthentication(null);
            throw new CustomException("Invalid username/password supplied", ResponseCode.SIGN_IN_ERROR.code);
        }
    }

    public String signup(User user)
    {
        if (!userRepository.existsByName(user.getName()))
        {
            user.setPasswd(passwordEncoder.encode(user.getPasswd()));
            User u = userRepository.save(user);
            return jwtTokenProvider.createToken(user.getName(), user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getAuthority())).collect(Collectors.toList()));
        }
        else
        {
            throw new CustomException("Username is already in use", ResponseCode.USER_ALREADY_USE_ERROR.code);
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
            throw new CustomException("The user doesn't exist", ResponseCode.ACCOUNT_USER_NOT_EXIST_ERROR.code);
        }
        return appUser;
    }

    public User whoami(HttpServletRequest req)
    {
        return userRepository.findByName(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }

    public String refresh(String username)
    {
        return jwtTokenProvider.createToken(username, userRepository.findByName(username).getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getAuthority())).collect(Collectors.toList()));
    }

    private void reloadUserAuthority(String username)
    {
        List<Role> authorityList = userRepository.findByName(username).getRoles();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        User principal = (User) authentication.getPrincipal();
        principal.setRoles(authorityList);

        // 重新new一个token，因为Authentication中的权限是不可变的.
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                principal, authentication.getCredentials(),
                authorityList);
        result.setDetails(authentication.getDetails());
        securityContext.setAuthentication(result);
    }
}
