//package com.yk.base.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//
//@EnableWebSecurity
//public class SecurityConfig2 extends WebSecurityConfigurerAdapter
//{
//    @Autowired
//    private DataSource dataSource;
//    @Resource
//    private UserDetailsService userDetailsService;
//
//    //实现用户身份认证
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception
//    {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception
//    {
//        //配置url的访问权限
//        http.authorizeRequests()
//                .antMatchers("/**").permitAll()
//                .anyRequest().authenticated();
//        //关闭csrf保护功能
//        http.csrf().disable();
//        //使用自定义的登录窗口
//        http.formLogin()
//                .loginPage("/userLogin").permitAll() //userLogin对应控制器中的自定义登录路径
//                .usernameParameter("username").passwordParameter("password")  //username和password对应前端表单的name键
//                .defaultSuccessUrl("/") //登录成功后跳转
//                .failureUrl("/userLogin?error"); //登录失败跳转
//        //实现注销 （会清空session）
//        http.logout()
//                .logoutUrl("/mylogout") //只能是post方法，/mylogout是前端的请求
//                .logoutSuccessUrl("/userLogin");
//        http.rememberMe()
//                .rememberMeParameter("rememberme").tokenValiditySeconds(200) //200秒
//                .tokenRepository(tokenRepository()); //配置持久化token
//    }
//
//    /**
//     * 持久化token存储
//     * 数据库的表必须是persistent_logins ，字段必须是username、series(序列号)、token、last_used（更新时间）
//     */
//    @Bean
//    public JdbcTokenRepositoryImpl tokenRepository()
//    {
//        JdbcTokenRepositoryImpl jr = new JdbcTokenRepositoryImpl();
//        jr.setDataSource(dataSource);
//        return jr;
//    }
//}
