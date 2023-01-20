package com.jinjjaseoul.auth.handler;

import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserPrincipal;
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

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RedisUtils redisUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 권한이 없어 강제로 인터셉트 당했을 경우 (ex. 관리자 페이지로 접근 시도 -> 로그인 페이지)
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache(); // SavedRequest 객체를 세션에 저장하는 역할
        SavedRequest savedRequest = requestCache.getRequest(request, response); // 현재 클라이언트의 요청 과정 중에 포함된 쿠키, 헤더, 파라미터 값들을 추출하여 보관하는 역할

        // 로그인 버튼을 눌러 접속했을 경우
        String prevPage = CookieUtils.getCookie(request, "prevPage")
                .map(Cookie::getValue)
                .orElse(null);

        String targetUrl = getDefaultTargetUrl();

        // 강제 인터셉트 당했을 경우
        if (savedRequest != null) {
            targetUrl = savedRequest.getRedirectUrl();
            requestCache.removeRequest(request, response);

        // 직접 로그인 페이지로 접속했을 경우
        } else if (StringUtils.hasText(prevPage)) {
            targetUrl = prevPage;
            CookieUtils.deleteCookie(request, response, "prevPage");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        TokenResponseDto tokenResponseDto = jwtService.createTokenDto(userPrincipal.getUsername(), userPrincipal.getRole());

        saveAccessTokenOnResponse(response, tokenResponseDto);
        saveRefreshTokenOnRedis(userPrincipal, tokenResponseDto);
        CookieUtils.addCookie(response, "atk", EncodingUtils.encodeURIComponent(tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken()), 1800);

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveAccessTokenOnResponse(HttpServletResponse response, TokenResponseDto tokenResponseDto) {
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Authorization", tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken());
    }

    private void saveRefreshTokenOnRedis(UserPrincipal userPrincipal, TokenResponseDto tokenResponseDto) {
        redisUtils.setValue(userPrincipal.getUsername(), tokenResponseDto.getRefreshToken(), Duration.ofMillis(tokenResponseDto.getRefreshTokenValidTime()));
    }
}