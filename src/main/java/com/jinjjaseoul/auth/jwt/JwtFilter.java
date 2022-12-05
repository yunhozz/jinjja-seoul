package com.jinjjaseoul.auth.jwt;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.utils.CookieUtils;
import com.jinjjaseoul.domain.user.model.entity.UserRefreshToken;
import com.jinjjaseoul.domain.user.model.repository.UserRefreshTokenRepository;
import com.jinjjaseoul.domain.user.service.exception.JwtTokenNotFoundException;
import io.jsonwebtoken.lang.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.jinjjaseoul.auth.oauth2.OAuth2AuthorizationRequestRepository.REFRESH_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final int COOKIE_MAX_AGE = 60 * 60 * 24 * 14; // 2 weeks

    @Override
    @Transactional
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[Verifying Token]");
        log.info("uri = " + request.getRequestURI());

        resolveToken(request).ifPresent(accessToken -> {
            if (jwtService.isValidatedToken(accessToken)) {
                if (jwtService.isExpiredToken(accessToken)) {
                    CookieUtils.getCookie(request, REFRESH_TOKEN).ifPresent(cookie -> {
                        String userRefreshTokenId = cookie.getValue();
                        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findById(Long.valueOf(userRefreshTokenId))
                                .orElseThrow(JwtTokenNotFoundException::new);
                        Authentication authentication = jwtService.getAuthentication(userRefreshToken.getRefreshToken());
                        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                        TokenResponseDto tokenResponseDto = jwtService.createTokenDto(userPrincipal.getUsername(), userPrincipal.getRole());
                        setAuthentication(tokenResponseDto.getAccessToken());
                        userRefreshToken.updateRefreshToken(tokenResponseDto.getRefreshToken());
                        saveTokenOnResponseAndCookie(request, response, tokenResponseDto, userRefreshToken);
                    });

                } else setAuthentication(accessToken);
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

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void saveTokenOnResponseAndCookie(HttpServletRequest request, HttpServletResponse response, TokenResponseDto tokenResponseDto, UserRefreshToken userRefreshToken) {
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Authorization", tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken());
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtils.addCookie(response, REFRESH_TOKEN, String.valueOf(userRefreshToken.getId()), COOKIE_MAX_AGE);
    }
}