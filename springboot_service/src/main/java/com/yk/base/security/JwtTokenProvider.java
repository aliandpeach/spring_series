package com.yk.base.security;

import com.yk.base.exception.CustomException;
import com.yk.base.exception.ResponseCode;
import com.yk.db.jpa.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider
{
    /**
     * THIS IS NOT A SECURE PRACTICE! For simplicity, we are storing a static key here. Ideally, in a
     * microservices environment, this key would be kept on a config-server.
     */
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private final long validityInMilliseconds = 3600000; // 1h

    @Getter
    @Value("${secret.related.excepts}")
    private String excepts;

    @Getter
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    protected void init()
    {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, List<GrantedAuthority> roles)
    {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", roles.stream()
                .filter(Objects::nonNull)
                .map(s -> new SimpleGrantedAuthority(s.getAuthority()))
                .collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token)
    {
//        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(getUsername(token), null, getAuths(token));
    }

    public String getUsername(String token)
    {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public List<GrantedAuthority> getAuths(String token)
    {
        Claims _claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        if (null == _claims)
        {
            return new ArrayList<>();
        }
        List<?> auths = _claims.get("auth", List.class);
        if (null == auths)
        {
            return new ArrayList<>();
        }
        return auths.stream().filter(f -> f instanceof LinkedHashMap && null != ((LinkedHashMap) f).get("authority"))
                .map(a -> new SimpleGrantedAuthority(((LinkedHashMap) a).get("authority").toString())).collect(Collectors.toList());
    }

    public String resolveToken(HttpServletRequest req)
    {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer "))
        {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token)
    {
        try
        {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        }
        catch (ExpiredJwtException expiredJwtException)
        {
            throw new CustomException(ResponseCode.ACCOUNT_TOKEN_EXPIRED_ERROR.message, ResponseCode.ACCOUNT_TOKEN_EXPIRED_ERROR.code);
        }
        catch (JwtException e)
        {
            throw new CustomException(ResponseCode.ACCOUNT_TOKEN_INVALID_ERROR.message, ResponseCode.ACCOUNT_TOKEN_INVALID_ERROR.code);
        }
    }
}
