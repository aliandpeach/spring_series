package com.yk.base.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)// @PreAuthorize生效
//@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public WebSecurityConfig()
    {
        // 设置为true, 默认的拦截器不会开启, 避免内部执行getSession后产生session
        // 但其实我只需要移除session相关的, 因此只需要移除http.sessionManagement().disable()和http.securityContext().disable()即可
        // super(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable();
        http.formLogin().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.sessionManagement().disable();
        // 移除SecurityContextPersistenceFilter及其内部操作session的相关类的配置
        http.securityContext().disable();

        String[] excepts = Optional.ofNullable(jwtTokenProvider.getExcepts()).orElse("").split(";");

        // Entry points
        http.authorizeRequests()
                .antMatchers(excepts).permitAll()
                .antMatchers("/h2-console/**/**").permitAll()
                .anyRequest().authenticated();

        // If a user try to access a resource without having enough permissions
        http.exceptionHandling().accessDeniedPage("/login");

        http.exceptionHandling().authenticationEntryPoint(new SpringSecurityAuthenticationEntryPoint());
        http.exceptionHandling().accessDeniedHandler(new SpringSecurityAccessDeniedHandler());

//        http.exceptionHandling(new Customizer<ExceptionHandlingConfigurer<HttpSecurity>>()
//        {
//            @Override
//            public void customize(ExceptionHandlingConfigurer<HttpSecurity> httpSecurityExceptionHandlingConfigurer)
//            {
//                System.out.println();
//            }
//        });

        // Apply JWT
        http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider, authenticationManager));
    }

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
        auth.userDetailsService(jwtTokenProvider.getUserDetailsService());
    }
}
