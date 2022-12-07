package com.jinjjaseoul.auth.jwt;

import com.jinjjaseoul.auth.model.UserDetailsServiceImpl;
import com.jinjjaseoul.common.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService implements InitializingBean {

    private final UserDetailsServiceImpl userDetailsService;

    @Value("${jinjja-seoul.jwt.secret}")
    private String secretKey;

    @Value("${jinjja-seoul.jwt.grantType}")
    private String grantType;

    @Value("${jinjja-seoul.jwt.accessTime}")
    private Long accessTokenValidMilliSecond;

    @Value("${jinjja-seoul.jwt.refreshTime}")
    private Long refreshTokenValidMilliSecond;

    private final String ACCESS_TOKEN_TYPE = "atk";
    private final String REFRESH_TOKEN_TYPE = "rtk";

    @Override
    public void afterPropertiesSet() throws Exception {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponseDto createTokenDto(String email, Role role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role.getValue());
        Date now = new Date();

        String accessToken = createToken(claims, ACCESS_TOKEN_TYPE, now, accessTokenValidMilliSecond);
        String refreshToken = createToken(claims, REFRESH_TOKEN_TYPE, now, refreshTokenValidMilliSecond);

        return TokenResponseDto.builder()
                .grantType(grantType)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenValidTime(refreshTokenValidMilliSecond)
                .build();
    }

    public Authentication getAuthentication(String token) {
        String email = parseToken(token).getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Long getExpirationTime(String token) {
        Long now = new Date().getTime();
        Long expirationTime = parseToken(token).getExpiration().getTime();

        return expirationTime - now;
    }

    public boolean isValidatedToken(String token) {
        try {
            parseToken(token);
            return true;

        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");

        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 Jwt 서명입니다.");

        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다.");

        } catch (IllegalArgumentException e) {
            log.error("잘못된 토큰입니다.");
        }

        return false;
    }

    public boolean isRefreshToken(String token) {
        return parseToken(token).get("type").equals(REFRESH_TOKEN_TYPE);
    }

    private String createToken(Claims claims, String tokenType, Date date, Long time) {
        claims.put("type", tokenType);
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + time))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}