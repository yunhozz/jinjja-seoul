package com.jinjjaseoul.domain.user.service;

import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.jwt.TokenResponseDto;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.jinjjaseoul.common.utils.CookieUtils.COOKIE_MAX_AGE;
import static com.jinjjaseoul.common.utils.CookieUtils.REFRESH_TOKEN;

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
        userRepository.findByEmail(loginRequestDto.getEmail()).ifPresentOrElse(user -> {
            validatePasswordMatch(loginRequestDto, user);
            tokenResponseDto[0] = jwtService.createTokenDto(user.getEmail(), user.getRole());
            UserRefreshToken userRefreshToken = new UserRefreshToken(user.getId(), tokenResponseDto[0].getRefreshToken());
            userRefreshTokenRepository.save(userRefreshToken);

            saveTokenOnResponseAndCookie(request, response, tokenResponseDto[0], userRefreshToken);
            session.setAttribute("userInfo", new SessionUser(user.getEmail(), user.getName()));

        }, () -> {
            throw new EmailNotFoundException();
        });

        return tokenResponseDto[0];
    }

    // TODO: 2022-12-04 Redis 를 사용하여 로그아웃 구현
    @Transactional
    public void logout(Long userId, HttpServletRequest request, HttpServletResponse response) {
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserId(userId)
                .orElseThrow(JwtTokenNotFoundException::new);
        userRefreshTokenRepository.delete(userRefreshToken);

        deleteTokenOnResponseAndCookie(request, response);
        session.removeAttribute("userInfo");
    }

    @Transactional
    public void withdraw(Long userId, HttpServletRequest request, HttpServletResponse response) {
        logout(userId, request, response);
        User user = userRepository.getReferenceById(userId);
        user.withdraw();
    }

    private void validatePasswordMatch(LoginRequestDto loginRequestDto, User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new PasswordDifferentException();
        }
    }

    private void saveTokenOnResponseAndCookie(HttpServletRequest request, HttpServletResponse response, TokenResponseDto tokenResponseDto, UserRefreshToken userRefreshToken) {
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Authorization", tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken());
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtils.addCookie(response, REFRESH_TOKEN, String.valueOf(userRefreshToken.getId()), COOKIE_MAX_AGE);
    }

    private void deleteTokenOnResponseAndCookie(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Authorization", "");
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
    }
}