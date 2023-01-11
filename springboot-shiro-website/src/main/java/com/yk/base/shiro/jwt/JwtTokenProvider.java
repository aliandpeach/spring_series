package com.yk.base.shiro.jwt;

import com.yk.base.exception.ShiroException;
import com.yk.base.shiro.redis.RedisServiceImpl;
import com.yk.user.model.Permission;
import com.yk.user.model.Role;
import com.yk.user.model.User;
import com.yk.user.service.UserService;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider
{
    private static Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    /**
     * THIS IS NOT A SECURE PRACTICE! For simplicity, we are storing a static key here. Ideally, in a
     * microservices environment, this key would be kept on a config-server.
     */
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    @Getter
    private long validityInMilliseconds = 3600000; // 1h

    @Getter
//    @Value("${secret.related.excepts}")
    private String excepts;

    @Autowired
    @Getter
    private UserService userService;

    @Autowired
    @Getter
    private RedisServiceImpl redisService;

    @PostConstruct
    protected void init()
    {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, List<Role> roles)
    {
        return create(username, roles, validityInMilliseconds);
    }

    public String createRefreshToken(String username, List<Role> roles)
    {
        return create(username, roles, validityInMilliseconds);
    }

    private String create(String username, List<Role> roles, long millis)
    {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", roles.stream()
                .filter(Objects::nonNull)
                .map(s -> s.getName() + ":" + s.getPermissionList().stream().filter(Objects::nonNull).map(Permission::getName).sorted().collect(Collectors.joining(",")))
                .collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + millis);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public AuthenticationInfo getJwtAuthenticationToken(String token, String realmName)
    {
        String _username;
        try
        {
            _username = getUsername(token);
        }
        catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e)
        {
            if (e instanceof ExpiredJwtException)
            {
                // 在这里不直接抛出异常, 如果只是token过期但可以获得token的username, 就继续走下去, 在TokenFilter中进行判断是否返回refresh token
                Claims claims = ((ExpiredJwtException) e).getClaims();
                _username = null != claims ? claims.getSubject() : null;
                logger.error("token expired warning, token:{}, username : {}", token, _username);
            }
            else
            {
                throw new ShiroException(e.getMessage());
            }
        }
        User user = userService.queryUserByUsername(_username);
        if (null == user || StringUtils.isEmpty(user.getUsername()))
        {
            throw new ShiroException(400, "user not exist");
        }
        return new SimpleAuthenticationInfo(user.getUsername(), token, realmName);
    }

    public String getUsername(String token)
    {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        String subject = claimsJws.getBody().getSubject();
        return subject;
    }

    public String resolveToken(HttpServletRequest req)
    {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer "))
        {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    public boolean validateToken(String token)
    {
        try
        {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        }
        catch (SignatureException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e)
        {
            throw new UnsupportedTokenException("token error: unsupported");
        }
        catch (ExpiredJwtException e)
        {
            throw new ExpiredCredentialsException("token expired");
        }
        catch (Exception e)
        {
            throw new UnsupportedTokenException("token error");
        }
    }
}
