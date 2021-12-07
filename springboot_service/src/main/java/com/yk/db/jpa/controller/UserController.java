package com.yk.db.jpa.controller;

import com.yk.db.jpa.dto.UserDataDTO;
import com.yk.db.jpa.model.Role;
import com.yk.db.jpa.model.User;
import com.yk.db.jpa.repository.GroupRepository;
import com.yk.db.jpa.repository.RoleRepository;
import com.yk.db.jpa.repository.UserRepository;
import com.yk.db.jpa.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/06 10:58:01
 */
@RestController
@RequestMapping("/api")
@Api
public class UserController implements InitializingBean
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

//    @Autowired
//    private ModelMapper modelMapper;

    @RequestMapping("/query/user/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> queryAllUser()
    {
        List<User> users = userRepository.findAll();
        return users;
    }

    @RequestMapping("/query/role/all")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public List<Role> queryAllRole()
    {
        List<Role> roles = roleRepository.findAll();
        return roles;
    }

    @PostMapping("/signin")
    @ApiOperation(value = "${UserController.signin}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid name/passwd supplied")})
    public String login(@ApiParam("name") @RequestBody @Validated UserDataDTO user)
    {
        return userService.signin(user.getName(), user.getPasswd());
    }

    @PostMapping("/signup")
    @ApiOperation(value = "${UserController.signup}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 422, message = "name is already in use")})
    // MyBatisConfiguration 的DataSourceTransactionManager 默认使用在JPA上会无法生效， 所以这里需要特别指定JPA自己的自定义事务名
    // 不指定事务的时候，JPA会默认使用 transactionManager bean
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String signup(@ApiParam("Signup User") @RequestBody UserDataDTO userDataDTO)
    {
        User user = new User();
        List<Role> roles = new ArrayList<>(Collections.singleton(roleRepository.findRoleByName("ROLE_CLIENT")));
        user.setRoles(roles);
        user.setGroup(groupRepository.findByName("GROUP_CLIENT"));
        user.setName(userDataDTO.getName());
        user.setPasswd(userDataDTO.getPasswd());
        return userService.signup(user);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
    }

    /*@DeleteMapping(value = "/{name}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${UserController.delete}", authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public String delete(@ApiParam("name") @PathVariable String name)
    {
        userService.delete(name);
        return name;
    }

    @GetMapping(value = "/{name}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "${UserController.search}", response = UserResponseDTO.class, authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public UserResponseDTO search(@ApiParam("name") @PathVariable String name)
    {
        return modelMapper.mapComposedModel(userService.search(name), UserResponseDTO.class);
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "${UserController.me}", response = UserResponseDTO.class, authorizations = {@Authorization(value = "apiKey")})
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public UserResponseDTO whoami(HttpServletRequest request)
    {
        return modelMapper.mapComposedModel(userService.whoami(request), UserResponseDTO.class);
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public String refresh(HttpServletRequest request)
    {
        return userService.refresh(request.getRemoteUser());
    }*/
}