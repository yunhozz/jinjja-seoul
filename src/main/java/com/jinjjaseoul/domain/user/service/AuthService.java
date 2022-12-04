package com.jinjjaseoul.domain.user.service;

import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.jwt.TokenResponseDto;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.auth.oauth2.SessionUser;
import com.jinjjaseoul.common.utils.CookieUtils;
import com.jinjjaseoul.domain.user.dto.LoginRequestDto;
import com.jinjjaseoul.domain.user.model.entity.User;
import com.jinjjaseoul.domain.user.model.entity.UserRefreshToken;
import com.jinjjaseoul.domain.user.model.repository.UserRefreshTokenRepository;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import com.jinjjaseoul.domain.user.service.exception.EmailNotFoundException;
import com.jinjjaseoul.domain.user.service.exception.JwtTokenNotFoundException;
import com.jinjjaseoul.domain.user.service.exception.PasswordDifferentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.jinjjaseoul.auth.oauth2.OAuth2AuthorizationRequestRepository.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final JwtService jwtService;
    private final HttpSession session;

    @Value("${jinjja-seoul.jwt.grantType}")
    private String grantType;

    @Transactional
    public TokenResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        final TokenResponseDto[] tokenResponseDto = {null};
        userRepository.findByEmail(loginRequestDto.getEmail())
                .ifPresentOrElse(user -> {
                    validatePasswordMatch(loginRequestDto, user);
                    tokenResponseDto[0] = jwtService.createTokenDto(user.getEmail(), user.getRole());
                    UserRefreshToken userRefreshToken = new UserRefreshToken(user.getId(), tokenResponseDto[0].getRefreshToken());
                    userRefreshTokenRepository.save(userRefreshToken);

                    saveTokenOnResponseAndCookie(request, response, tokenResponseDto[0]);
                    session.setAttribute("userInfo", new SessionUser(user.getEmail(), user.getName()));

                }, () -> {
                    throw new EmailNotFoundException();
                });

        return tokenResponseDto[0];
    }

    @Transactional
    public TokenResponseDto tokenReissue(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = CookieUtils.getCookie(request, REFRESH_TOKEN)
                .orElseThrow(() -> new InvalidCookieException("찾으려는 쿠키가 존재하지 않습니다."));
        String refreshToken = cookie.getValue();

        Authentication authentication = jwtService.getAuthentication(refreshToken);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserId(userPrincipal.getId())
                .orElseThrow(JwtTokenNotFoundException::new);

        TokenResponseDto tokenResponseDto = jwtService.createTokenDto(userPrincipal.getUsername(), userPrincipal.getRole());
        userRefreshToken.updateRefreshToken(tokenResponseDto.getRefreshToken());
        saveTokenOnResponseAndCookie(request, response, tokenResponseDto);

        return tokenResponseDto;
    }

    // TODO: 2022-12-04 Redis 를 사용하여 로그아웃 구현
    // 임시 방편으로 Authorization 헤더값 수정, 재발급 토큰 DB 삭제, 세션&쿠키 삭제
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = CookieUtils.getCookie(request, REFRESH_TOKEN)
                .orElseThrow(() -> new InvalidCookieException("찾으려는 쿠키가 존재하지 않습니다."));
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByRefreshToken(grantType + cookie.getValue())
                .orElseThrow(JwtTokenNotFoundException::new);
        userRefreshTokenRepository.delete(userRefreshToken);

        deleteTokenOnResponseAndCookie(request, response);
        session.removeAttribute("userInfo");
    }

    @Transactional
    public void withdraw(Long userId, HttpServletRequest request, HttpServletResponse response) {
        logout(request, response);
        User user = userRepository.getReferenceById(userId);
        user.withdraw();
    }

    private void validatePasswordMatch(LoginRequestDto loginRequestDto, User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new PasswordDifferentException();
        }
    }

    private void saveTokenOnResponseAndCookie(HttpServletRequest request, HttpServletResponse response, TokenResponseDto tokenResponseDto) {
        // access token
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Authorization", tokenResponseDto.getAccessToken());
        // refresh token
        String refreshToken = tokenResponseDto.getRefreshToken().split(" ")[1];
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtils.addCookie(response, REFRESH_TOKEN, refreshToken, tokenResponseDto.getRefreshTokenValidTime().intValue());
    }

    private void deleteTokenOnResponseAndCookie(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Authorization", "");
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
    }
}