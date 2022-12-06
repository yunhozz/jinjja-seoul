package com.jinjjaseoul.domain.user.service;

import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.jwt.TokenResponseDto;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.converter.UserConverter;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.user.dto.LoginRequestDto;
import com.jinjjaseoul.domain.user.dto.UserResponseDto;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.UserRepository;
import com.jinjjaseoul.domain.user.service.exception.AlreadyLoginException;
import com.jinjjaseoul.domain.user.service.exception.EmailNotFoundException;
import com.jinjjaseoul.domain.user.service.exception.JwtTokenNotFoundException;
import com.jinjjaseoul.domain.user.service.exception.PasswordDifferentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RedisUtils redisUtils;
    private final HttpSession session;

    @Value("${jinjja-seoul.session.key}")
    private String SESSION_KEY;

    private final String ACCESS_TOKEN_REDIS_DATA = "logout";

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        final TokenResponseDto[] tokenResponseDto = {null};
        userRepository.findByEmail(loginRequestDto.getEmail()).ifPresentOrElse(user -> {
            UserResponseDto userResponseDto = UserConverter.convertToDto(user);
            validatePasswordAndLoginCondition(loginRequestDto, userResponseDto);
            tokenResponseDto[0] = jwtService.createTokenDto(userResponseDto.getEmail(), userResponseDto.getRole());

            redisUtils.setValues(userResponseDto.getEmail(), tokenResponseDto[0].getRefreshToken(), Duration.ofMillis(tokenResponseDto[0].getRefreshTokenValidTime()));
            saveTokenOnResponse(response, tokenResponseDto[0]);
            session.setAttribute(SESSION_KEY, userResponseDto.getEmail());

        }, () -> {
            throw new EmailNotFoundException();
        });

        return tokenResponseDto[0];
    }

    // access token 만료 -> 세션에 저장된 이메일 정보 요청 -> redis 에 저장된 refresh token 을 이용하여 재발급 요청
    public TokenResponseDto tokenReissue(HttpServletResponse response) {
        String email = (String) session.getAttribute(SESSION_KEY);
        String refreshToken = redisUtils.getValues(email)
                .orElseThrow(JwtTokenNotFoundException::new);

        Authentication authentication = jwtService.getAuthentication(refreshToken);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        TokenResponseDto tokenResponseDto = jwtService.createTokenDto(userPrincipal.getUsername(), userPrincipal.getRole());
        updateRedisData(userPrincipal, userPrincipal.getUsername(), tokenResponseDto.getRefreshToken(), tokenResponseDto.getRefreshTokenValidTime());
        saveTokenOnResponse(response, tokenResponseDto);

        return tokenResponseDto;
    }

    // refresh token 삭제, access token 값을 redis 에 key 로 저장
    // 로그아웃된 access token 으로 요청이 들어왔을 때, 해당 토큰의 유효성이 남아있는 동안은 redis 에 블랙리스트로 등록되어 있을 것이기 때문에 로그인 불가
    public void logout(String accessToken, UserPrincipal userPrincipal) {
        String token = accessToken.split(" ")[1];
        Long expirationTime = jwtService.getExpirationTime(token);

        updateRedisData(userPrincipal, token, ACCESS_TOKEN_REDIS_DATA, expirationTime);
        session.removeAttribute(SESSION_KEY);
    }

    @Transactional
    public void withdraw(String accessToken, UserPrincipal userPrincipal) {
        logout(accessToken, userPrincipal);
        User user = userRepository.getReferenceById(userPrincipal.getId());
        user.withdraw();
    }

    private void validatePasswordAndLoginCondition(LoginRequestDto loginRequestDto, UserResponseDto userResponseDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String email = (String) session.getAttribute(SESSION_KEY);

        if (!encoder.matches(loginRequestDto.getPassword(), userResponseDto.getPassword())) {
            throw new PasswordDifferentException();
        }

        if (email != null && email.equals(loginRequestDto.getEmail())) {
            throw new AlreadyLoginException();
        }
    }

    private void updateRedisData(UserPrincipal userPrincipal, String key, String data, Long timeMills) {
        redisUtils.deleteValues(userPrincipal.getUsername());
        redisUtils.setValues(key, data, Duration.ofMillis(timeMills));
    }

    private void saveTokenOnResponse(HttpServletResponse response, TokenResponseDto tokenResponseDto) {
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Authorization", tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken());
    }
}