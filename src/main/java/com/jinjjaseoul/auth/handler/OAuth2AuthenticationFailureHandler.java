package com.jinjjaseoul.auth.handler;

import com.jinjjaseoul.common.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String targetUri = CookieUtils.getCookie(request, OAuth2AuthorizationRequestRepository.OAUTH2_AUTHORIZATION_REQUEST)
                .map(Cookie::getValue)
                .orElse("/");

        exception.printStackTrace();
        targetUri = UriComponentsBuilder.fromUriString(targetUri)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        oAuth2AuthorizationRequestRepository.removeAuthorizationRequest(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUri);
    }
}