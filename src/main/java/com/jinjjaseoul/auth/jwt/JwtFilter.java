package com.jinjjaseoul.auth.jwt;

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
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RedisUtils redisUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[Verifying Token]");
        log.info("uri = " + request.getRequestURI());

        resolveToken(request).ifPresent(accessToken -> {
            if (jwtService.isValidatedToken(accessToken)) {
                String logout = redisUtils.getValues(accessToken)
                        .orElse(null);
                // access token 로그아웃 상태 확인
                if (!StringUtils.hasText(logout)) {
                    Authentication authentication = jwtService.getAuthentication(accessToken);
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
}