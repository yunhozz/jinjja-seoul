package com.jinjjaseoul.auth.jwt;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.TokenResponseDto;
import com.jinjjaseoul.common.utils.RedisUtils;
import io.jsonwebtoken.lang.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

import static com.jinjjaseoul.auth.jwt.JwtService.ACCESS_TOKEN_TYPE;
import static com.jinjjaseoul.auth.jwt.JwtService.REFRESH_TOKEN_TYPE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RedisUtils redisUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[Verifying Token]");
        log.info("url = " + request.getRequestURL());

        resolveToken(request).ifPresent(token -> {
            if (jwtService.isValidatedToken(token)) {
                String logout = redisUtils.getValue(token).orElse(null);
                String requestURI = request.getRequestURI();

                // access token 만료 시 refresh token 검증
                if (jwtService.getTokenType(token).equals(REFRESH_TOKEN_TYPE) && !requestURI.equals("/api/auth/issue")) {
                    throw new IllegalStateException("JWT 토큰을 확인해주세요.");
                }

                // access token 만료 5분전 재발급 로직 생성
                if (jwtService.getTokenType(token).equals(ACCESS_TOKEN_TYPE) && jwtService.getExpirationTime(token) < 30000) {
                    log.info("===== token reissue =====");
                    Authentication authentication = jwtService.getAuthentication(token);
                    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                    String refreshToken = redisUtils.getValue(userPrincipal.getUsername())
                            .orElseThrow(() -> new IllegalStateException("재발급 토큰이 존재하지 않습니다."));

                    TokenResponseDto tokenResponseDto = jwtService.tokenReissue(refreshToken);
                    token = tokenResponseDto.getAccessToken();
                    saveTokenOnResponse(response, tokenResponseDto);
                    updateRedisData(userPrincipal, userPrincipal.getUsername(), tokenResponseDto.getRefreshToken(), tokenResponseDto.getRefreshTokenValidTime());
                }

                // access token 로그아웃 상태 확인 -> SecurityContext 에 인증객체 저장 -> @AuthenticationPrincipal
                if (!StringUtils.hasText(logout)) {
                    Authentication authentication = jwtService.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        });

        filterChain.doFilter(request, response);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return Strings.hasText(token) ? resolveParts(token) : Optional.empty();
    }

    private Optional<String> resolveParts(String token) {
        String[] parts = token.split(" ");
        return parts.length == 2 && parts[0].equals("Bearer") ? Optional.ofNullable(parts[1]) : Optional.empty();
    }

    private void saveTokenOnResponse(HttpServletResponse response, TokenResponseDto tokenResponseDto) {
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Authorization", tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken());
    }

    private void updateRedisData(UserPrincipal userPrincipal, String key, String data, Long timeMills) {
        redisUtils.deleteData(userPrincipal.getUsername());
        redisUtils.setValue(key, data, Duration.ofMillis(timeMills));
    }
}