//package com.yk.base.security;
//
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * 描述
// *
// * @author yangk
// * @version 1.0
// * @since 2021/11/18 19:10:29
// */
//public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter
//{
//    private AuthenticationManager authenticationManager;
//
//    public JWTAuthenticationFilter(AuthenticationManager authenticationManager)
//    {
//        this.authenticationManager = authenticationManager;
//        super.setFilterProcessesUrl("/api/signin");
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request,
//                                                HttpServletResponse response) throws AuthenticationException
//    {
//        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("admin", "123456"));
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request,
//                                            HttpServletResponse response,
//                                            FilterChain chain,
//                                            Authentication authResult) throws IOException, ServletException
//    {
//        System.out.println();
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException
//    {
//        response.getWriter().write("authentication failed, reason: " + failed.getMessage());
//    }
//}
