package com.jinjjaseoul.auth.handler;

import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.TokenResponseDto;
import com.jinjjaseoul.common.utils.CookieUtils;
import com.jinjjaseoul.common.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

import static com.jinjjaseoul.auth.handler.OAuth2AuthorizationRequestCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RedisUtils redisUtils;
    private final OAuth2AuthorizationRequestCookieRepository oAuth2AuthorizationRequestCookieRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        TokenResponseDto tokenResponseDto = jwtService.createTokenDto(userPrincipal.getUsername(), userPrincipal.getRole());

        String redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(getDefaultTargetUrl());

//        String targetUri = UriComponentsBuilder.fromUriString(redirectUri)
//                .queryParam("token", tokenResponseDto.getAccessToken())
//                .build().toString();

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + redirectUri);
            return;
        }

        saveAccessTokenOnResponse(response, tokenResponseDto);
        saveRefreshTokenOnRedis(userPrincipal, tokenResponseDto);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    private void saveAccessTokenOnResponse(HttpServletResponse response, TokenResponseDto tokenResponseDto) {
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Authorization", tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken());
    }

    private void saveRefreshTokenOnRedis(UserPrincipal userPrincipal, TokenResponseDto tokenResponseDto) {
        redisUtils.setValue(userPrincipal.getUsername(), tokenResponseDto.getRefreshToken(), Duration.ofMillis(tokenResponseDto.getRefreshTokenValidTime()));
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestCookieRepository.removeAuthorizationRequest(request, response);
    }
}