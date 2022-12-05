package com.jinjjaseoul.auth.oauth2;

import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.jwt.TokenResponseDto;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.utils.CookieUtils;
import com.jinjjaseoul.domain.user.model.entity.UserRefreshToken;
import com.jinjjaseoul.domain.user.model.repository.UserRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.jinjjaseoul.auth.oauth2.OAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.jinjjaseoul.auth.oauth2.OAuth2AuthorizationRequestRepository.REFRESH_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;
    private final int COOKIE_MAX_AGE = 60 * 60 * 24 * 14; // 2 weeks

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(getDefaultTargetUrl());

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        String redirectUrl = createRedirectUrl(request, response, authentication, targetUrl);
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String createRedirectUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication, String targetUrl) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        TokenResponseDto tokenResponseDto = jwtService.createTokenDto(userPrincipal.getUsername(), userPrincipal.getRole());

        UserRefreshToken userRefreshToken = saveOrUpdateRefreshToken(userPrincipal, tokenResponseDto);
        saveTokenOnResponseAndCookie(request, response, tokenResponseDto, userRefreshToken);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", tokenResponseDto.getAccessToken())
                .build().toString();
    }

    @Transactional
    private UserRefreshToken saveOrUpdateRefreshToken(UserPrincipal userPrincipal, TokenResponseDto tokenResponseDto) {
        final UserRefreshToken[] userRefreshTokens = {null};
        userRefreshTokenRepository.findByUserId(userPrincipal.getId()).ifPresentOrElse(userRefreshToken -> {
            userRefreshToken.updateRefreshToken(tokenResponseDto.getRefreshToken());
            userRefreshTokens[0] = userRefreshToken;

        }, () -> {
            UserRefreshToken userRefreshToken = new UserRefreshToken(userPrincipal.getId(), tokenResponseDto.getRefreshToken());
            userRefreshTokens[0] = userRefreshTokenRepository.save(userRefreshToken);
        });

        return userRefreshTokens[0];
    }

    private void saveTokenOnResponseAndCookie(HttpServletRequest request, HttpServletResponse response, TokenResponseDto tokenResponseDto, UserRefreshToken userRefreshToken) {
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Authorization", tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken());
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtils.addCookie(response, REFRESH_TOKEN, String.valueOf(userRefreshToken.getId()), COOKIE_MAX_AGE);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}