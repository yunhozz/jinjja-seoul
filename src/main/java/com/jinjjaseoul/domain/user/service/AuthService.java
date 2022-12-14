package com.jinjjaseoul.domain.user.service;

import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.TokenResponseDto;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.user.dto.request.LoginRequestDto;
import com.jinjjaseoul.domain.user.dto.response.UserResponseDto;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import com.jinjjaseoul.domain.user.service.exception.AlreadyLoginException;
import com.jinjjaseoul.domain.user.service.exception.EmailNotFoundException;
import com.jinjjaseoul.domain.user.service.exception.JwtTokenNotFoundException;
import com.jinjjaseoul.domain.user.service.exception.PasswordDifferentException;
import com.jinjjaseoul.domain.user.service.exception.RefreshTokenDifferentException;
import com.jinjjaseoul.domain.user.service.exception.TokenTypeNotValidException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

import static com.jinjjaseoul.auth.jwt.JwtService.ACCESS_TOKEN_TYPE;
import static com.jinjjaseoul.auth.jwt.JwtService.REFRESH_TOKEN_TYPE;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RedisUtils redisUtils;

    private final String ACCESS_TOKEN_REDIS_DATA = "logout";

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        final TokenResponseDto[] tokenResponseDto = {null};
        userRepository.findByEmail(loginRequestDto.getEmail()).ifPresentOrElse(user -> {
            UserResponseDto userResponseDto = new UserResponseDto(user);
            validatePasswordAndLoginCondition(loginRequestDto, userResponseDto);
            tokenResponseDto[0] = jwtService.createTokenDto(userResponseDto.getEmail(), userResponseDto.getRole());

            redisUtils.setValue(userResponseDto.getEmail(), tokenResponseDto[0].getRefreshToken(), Duration.ofMillis(tokenResponseDto[0].getRefreshTokenValidTime()));
            saveTokenOnResponse(response, tokenResponseDto[0]);

        }, () -> {
            throw new EmailNotFoundException();
        });

        return tokenResponseDto[0];
    }

    // access token ?????? -> ????????? -> Authorization ????????? refresh token ?????? -> redis ??? ??????????????? ?????? -> ?????????
    public TokenResponseDto reissue(String refreshToken, UserPrincipal userPrincipal, HttpServletResponse response) {
        String token = refreshToken.split(" ")[1];
        validateRefreshToken(token);

        String redisToken = redisUtils.getValue(userPrincipal.getUsername())
                .orElseThrow(JwtTokenNotFoundException::new);
        validateRedisToken(token, redisToken);

        TokenResponseDto tokenResponseDto = jwtService.createTokenDto(userPrincipal.getUsername(), userPrincipal.getRole());
        updateRedisData(userPrincipal, userPrincipal.getUsername(), tokenResponseDto.getRefreshToken(), tokenResponseDto.getRefreshTokenValidTime());
        saveTokenOnResponse(response, tokenResponseDto);

        return tokenResponseDto;
    }

    // refresh token ??????, access token ?????? redis ??? key ??? ??????
    // ??????????????? access token ?????? ????????? ???????????? ???, ?????? ????????? ???????????? ???????????? ????????? redis ??? ?????????????????? ???????????? ?????? ????????? ????????? ????????? ??????
    public void logout(String accessToken, UserPrincipal userPrincipal) {
        String token = accessToken.split(" ")[1];
        validateAccessToken(token);
        Long expirationTime = jwtService.getExpirationTime(token);
        updateRedisData(userPrincipal, token, ACCESS_TOKEN_REDIS_DATA, expirationTime);
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.getReferenceById(userId);
        user.withdraw(); // soft delete
    }

    private void validatePasswordAndLoginCondition(LoginRequestDto loginRequestDto, UserResponseDto userResponseDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(loginRequestDto.getPassword(), userResponseDto.getPassword())) {
            throw new PasswordDifferentException();
        }

        if (redisUtils.getValue(loginRequestDto.getEmail()).isPresent()) {
            throw new AlreadyLoginException();
        }
    }

    private void validateAccessToken(String token) {
        if (!jwtService.getTokenType(token).equals(ACCESS_TOKEN_TYPE)) {
            throw new TokenTypeNotValidException();
        }
    }

    private void validateRefreshToken(String token) {
        if (!jwtService.getTokenType(token).equals(REFRESH_TOKEN_TYPE)) {
            throw new TokenTypeNotValidException();
        }
    }

    private void validateRedisToken(String token, String redisToken) {
        if (!redisToken.equals(token)) {
            throw new RefreshTokenDifferentException();
        }
    }

    private void updateRedisData(UserPrincipal userPrincipal, String key, String data, Long timeMills) {
        redisUtils.deleteData(userPrincipal.getUsername());
        redisUtils.setValue(key, data, Duration.ofMillis(timeMills));
    }

    private void saveTokenOnResponse(HttpServletResponse response, TokenResponseDto tokenResponseDto) {
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Authorization", tokenResponseDto.getGrantType() + tokenResponseDto.getAccessToken());
    }
}