package com.jinjjaseoul.auth.handler;

import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.auth.oauth2.AppProperties;
import com.jinjjaseoul.common.dto.TokenResponseDto;
import com.jinjjaseoul.common.utils.CookieUtils;
import com.jinjjaseoul.common.utils.EncodingUtils;
import com.jinjjaseoul.common.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RedisUtils redisUtils;
    private final AppProperties appProperties;
    private final OAuth2AuthorizationRequestCookieRepository oAuth2AuthorizationRequestCookieRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        String targetUri = getDefaultTargetUrl();
        String prevPage = CookieUtils.getCookie(request, "prevPage")
                .map(Cookie::getValue)
                .orElse(null);

        if (prevPage != null) {
            request.getSession().removeAttribute("prevPage");
        }

        if (savedRequest != null) {
            targetUri = savedRequest.getRedirectUrl();
            requestCache.removeRequest(request, response);

        } else if (StringUtils.hasText(prevPage)) {
            targetUri = prevPage;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        TokenResponseDto tokenResponseDto = jwtService.createTokenDto(userPrincipal.getUsername(), userPrincipal.getRole());

        saveAccessTokenOnResponse(response, tokenResponseDto);
        saveRefreshTokenOnRedis(userPrincipal, tokenResponseDto);
        CookieUtils.addCookie(response, "atk", EncodingUtils.encodeURIComponent(tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken()), 1800);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUri);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOAuth2().getAuthorizedRedirectUris().stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
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