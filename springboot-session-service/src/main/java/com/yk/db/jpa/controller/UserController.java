package com.yk.db.jpa.controller;

import com.yk.base.exception.BaseResponse;
import com.yk.base.exception.CustomException;
import com.yk.base.exception.ResponseCode;
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
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;
//    @Autowired
//    private ModelMapper modelMapper;

    @PostMapping("/log/record")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ADMIN')")
    public BaseResponse<Map<String, String>> log(@RequestParam String log)
    {
        BaseResponse<Map<String, String>> br = new BaseResponse<>();
        LOGGER.error(log);
        br.setData(Collections.singletonMap("result", "success"));
        return br;
    }

    @GetMapping("/log/throw")
    public BaseResponse<Map<String, String>> log()
    {
        List<User> users = userRepository.findAll();
        List<Role> listRole = entityManager.createQuery("select r from Role r where r.name like :name", Role.class)
                .setParameter("name", "%ROLE_CLIENT%").getResultList();
        LOGGER.debug("");

        List<Role> roleList = entityManager.createNativeQuery("select * from t_session_role where name like :name", Role.class)
                .setParameter("name", "%ROLE_CLIENT%").getResultList();
        LOGGER.debug("");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> root = criteriaQuery.from(Role.class);
        // 封装查询条件
        Predicate predicate2 = criteriaBuilder.like(root.get("name").as(String.class), "%ROLE_CLIENT%");
        Predicate predicate3 = criteriaBuilder.like(root.get("name").as(String.class), "%ROLE_ADMIN%");
        Predicate predicate = criteriaBuilder.or(predicate2, predicate3);
        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("name")));
        // 执行查询
        TypedQuery<Role> typeQuery = entityManager.createQuery(criteriaQuery);
        List<Role> roleListCriteria = typeQuery.getResultList();


        CriteriaBuilder criteriaBuilder_1 = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery_1 = criteriaBuilder_1.createQuery(Role.class);
        Root<Role> root_1 = criteriaQuery_1.from(Role.class);
        // 封装查询条件
        Predicate predicate2_1 = criteriaBuilder_1.like(root_1.get("name").as(String.class), "%ROLE_CLIENT%");
        Predicate predicate3_1 = criteriaBuilder_1.like(root_1.get("name").as(String.class), "%ROLE_ADMIN%");
        Predicate predicate_1 = criteriaBuilder_1.or(predicate2_1, predicate3_1);
        criteriaQuery_1.where(predicate_1);
        criteriaQuery_1.orderBy(criteriaBuilder_1.desc(root_1.get("name")));
        // you must annotate the method with the @Transactional annotation
        // 这里用到Session,所以必须和springboot-session-service-hibernate工程中一样给方法加入事务
//        List<Role> roleListCriteria2 = entityManager.unwrap(Session.class).createQuery(criteriaQuery_1).getResultList();

        List<Role> roleListAll = roleRepository.findAll(new Specification<Role>()
        {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder)
            {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(criteriaBuilder.equal(root.get("name"), "ROLE_CLIENT"));
                if (predicateList.size() > 0)
                {
                    Predicate[] predicates = new Predicate[predicateList.size()];
                    for (int i = 0; i < predicates.length; i++)
                    {
                        predicates[i] = predicateList.get(i);
                    }
                    query.where(predicates);
                }
                query.orderBy(criteriaBuilder.desc(root.get("name")));
                return query.getRestriction();
            }
        });

        throw new CustomException(ResponseCode.USER_TEST_ERROR.message, ResponseCode.USER_TEST_ERROR.code);
    }

    @RequestMapping("/query/user/all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_VIP')")
    public BaseResponse<List<User>> queryAllUser()
    {
        BaseResponse<List<User>> br = new BaseResponse<>();
        List<User> users = userRepository.findAll();
        br.setData(users);
        return br;
    }

    @RequestMapping("/query/role/all")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public BaseResponse<List<Role>> queryAllRole()
    {
        BaseResponse<List<Role>> br = new BaseResponse<>();
        List<Role> roles = roleRepository.findAll();
        br.setData(roles);
        return br;
    }

    /**
     * 登录
     */
    @PostMapping("/signin")
    @ApiOperation(value = "${UserController.signin}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid name/passwd supplied")})
    public BaseResponse<Map<String, String>> login(@ApiParam("name") @RequestBody @Validated UserDataDTO user)
    {
        BaseResponse<Map<String, String>> br = new BaseResponse<>();
//        String result = userService.signin(user.getName(), user.getPasswd());
        LOGGER.info("sign in success");
        return br;
    }

    /**
     * 注册
     */
    @PostMapping("/signup")
    @ApiOperation(value = "${UserController.signup}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 422, message = "name is already in use")})
    // MyBatisConfiguration 的DataSourceTransactionManager 默认使用在JPA上会无法生效， 所以这里需要特别指定JPA自己的自定义事务名
    // 不指定事务的时候，JPA会默认使用 transactionManager bean
    public BaseResponse<Map<String, String>> signup(@ApiParam("Signup User") @RequestBody UserDataDTO userDataDTO)
    {
        BaseResponse<Map<String, String>> br = new BaseResponse<>();
        User user = new User();
        List<Role> roles = new ArrayList<>(Collections.singleton(roleRepository.findRoleByName("ROLE_CLIENT")));
        user.setRoles(roles);
        user.setGroup(groupRepository.findByName("GROUP_CLIENT"));
        user.setName(userDataDTO.getName());
        user.setPasswd(userDataDTO.getPasswd());
        userService.signup(user);
        return br;
    }

    /**
     * 该方法不能让管理员为其他用户更新权限, 只能是当前登录用户更新自己的权限(所以该接口意义不大)
     * <p>
     * 1. 可强制让在线用户下线
     * 2. 获取redis中所有在线用户, 修改某用户的权限信息
     *
     * @return BaseResponse
     */
    @GetMapping("/vip")
    public BaseResponse<Boolean> updateAuthentication(@RequestParam("type") @Validated int type)
    {
        BaseResponse<Boolean> br = new BaseResponse<>();
        if (type == 0)
        {
            // 得到当前的认证信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //  获取当前的所有授权
            List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
            // 添加 ROLE_VIP 授权
            updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_VIP"));
            // 生成新的认证信息
            Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
            // 重置认证信息, 这里更新后最终会调用到 HttpSessionSecurityContextRepository.SaveToSessionResponseWrapper.saveContext,
            // 判断context是否和之前的context一致, 如果不一致就httpSession.setAttribute更新
            // 其重点就在于SecurityContextPersistenceFilter 中执行repo.loadContext的时候, 在HttpSessionSecurityContextRepository中
            // 替换成了request和response分别替换成了SaveToSessionRequestWrapper和SaveToSessionResponseWrapper
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        if (type == 1)
        {
            // 得到当前的认证信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //  获取当前的所有授权
            List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
            // 移除VIP角色
            List<GrantedAuthority> newAuthorities = updatedAuthorities.stream().filter(a -> !"ROLE_VIP".equals(a.getAuthority())).collect(Collectors.toList());
            // 生成新的认证信息
            Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), newAuthorities);
            // 重置认证信息, 这里更新后最终会调用到 HttpSessionSecurityContextRepository.SaveToSessionResponseWrapper.saveContext,
            // 判断context是否和之前的context一致, 如果不一致就httpSession.setAttribute更新
            // 其重点就在于SecurityContextPersistenceFilter 中执行repo.loadContext的时候, 在HttpSessionSecurityContextRepository中
            // 替换成了request和response分别替换成了SaveToSessionRequestWrapper和SaveToSessionResponseWrapper
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        br.setData(true);
        return br;
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
