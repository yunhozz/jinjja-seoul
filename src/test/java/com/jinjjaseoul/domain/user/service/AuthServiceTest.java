package com.jinjjaseoul.domain.user.service;

import com.jinjjaseoul.auth.jwt.JwtService;
import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.TokenResponseDto;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.user.dto.request.LoginRequestDto;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import com.jinjjaseoul.domain.user.service.exception.AlreadyLoginException;
import com.jinjjaseoul.domain.user.service.exception.EmailNotFoundException;
import com.jinjjaseoul.domain.user.service.exception.JwtTokenNotFoundException;
import com.jinjjaseoul.domain.user.service.exception.PasswordDifferentException;
import com.jinjjaseoul.domain.user.service.exception.RefreshTokenDifferentException;
import com.jinjjaseoul.domain.user.service.exception.TokenTypeNotValidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Optional;

import static com.jinjjaseoul.auth.jwt.JwtService.ACCESS_TOKEN_TYPE;
import static com.jinjjaseoul.auth.jwt.JwtService.REFRESH_TOKEN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtService jwtService;

    @Mock
    RedisUtils redisUtils;

    @Mock
    HttpServletResponse response;

    @Test
    @DisplayName("로그인 시 jwt 토큰 dto 발급")
    void login() throws Exception {
        // given
        User user = createUser();
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@gmail.com", "123");
        TokenResponseDto tokenResponseDto = createTokenDto();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(redisUtils.getValue(anyString())).willReturn(Optional.empty());
        given(jwtService.createTokenDto(anyString(), any(Role.class))).willReturn(tokenResponseDto);

        willDoNothing().given(redisUtils).setValue(anyString(), anyString(), any(Duration.class));

        // when
        TokenResponseDto result = authService.login(loginRequestDto, response);

        // then
        assertThat(result.getGrantType()).isEqualTo("Bearer");
        assertThat(result.getAccessToken()).isEqualTo("atk");
        assertThat(result.getRefreshToken()).isEqualTo("rtk");
    }

    @Test
    @DisplayName("로그인 시 이메일 검증 실패")
    void loginThrowsEmailException() throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("wrong@gmail.com", "123");
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // then
        assertThrows(EmailNotFoundException.class, () -> authService.login(loginRequestDto, response));
    }

    @Test
    @DisplayName("로그인 시 비밀번호 검증 실패")
    void loginThrowPasswordException() throws Exception {
        // given
        User user = createUser();
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@gmail.com", "wrong");

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // then
        assertThrows(PasswordDifferentException.class, () -> authService.login(loginRequestDto, response));
    }

    @Test
    @DisplayName("이미 로그인 상태에서 로그인 시도")
    void loginThrowAlreadyLoginException() throws Exception {
        // given
        User user = createUser();
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@gmail.com", "123");

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(redisUtils.getValue(anyString())).willReturn(Optional.of("atk"));

        // then
        assertThrows(AlreadyLoginException.class, () -> authService.login(loginRequestDto, response));
    }

    @Test
    @DisplayName("jwt 토큰 재발급")
    void reissue() throws Exception {
        // given
        User user = createUser();
        UserPrincipal userPrincipal = new UserPrincipal(user);
        TokenResponseDto tokenResponseDto = createTokenDto();
        String refreshToken = "refreshToken";

        given(jwtService.getTokenType(anyString())).willReturn(REFRESH_TOKEN_TYPE);
        given(jwtService.getAuthentication(anyString())).willReturn(new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities()));
        given(redisUtils.getValue(anyString())).willReturn(Optional.of(refreshToken));
        given(jwtService.createTokenDto(anyString(), any(Role.class))).willReturn(tokenResponseDto);

        willDoNothing().given(redisUtils).deleteData(anyString());
        willDoNothing().given(redisUtils).setValue(anyString(), anyString(), any(Duration.class));

        // when
        TokenResponseDto result = authService.reissue("Bearer " + refreshToken, response);

        // then
        assertThat(result.getAccessToken()).isEqualTo("atk");
        assertThat(result.getRefreshToken()).isEqualTo("rtk");
    }

    @Test
    @DisplayName("jwt 토큰 재발급 시 타입 불일치")
    void reissueThrowTypeException() throws Exception {
        // given
        String refreshToken = "refreshToken";
        given(jwtService.getTokenType(anyString())).willReturn("wrong-type");

        // then
        assertThrows(TokenTypeNotValidException.class, () -> authService.reissue("Bearer " + refreshToken, response));
    }

    @Test
    @DisplayName("jwt 토큰 재발급 시 Redis 에 해당 토큰 부재")
    void reissueThrowRedisTokenException() throws Exception {
        // given
        User user = createUser();
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String refreshToken = "refreshToken";

        given(jwtService.getTokenType(anyString())).willReturn(REFRESH_TOKEN_TYPE);
        given(jwtService.getAuthentication(anyString())).willReturn(new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities()));
        given(redisUtils.getValue(anyString())).willReturn(Optional.empty());

        // then
        assertThrows(JwtTokenNotFoundException.class, () -> authService.reissue("Bearer " + refreshToken, response));
    }

    @Test
    @DisplayName("jwt 토큰 발급 시 Redis 의 토큰과 불일치")
    void reissueThrowRedisTokenNotEqualException() throws Exception {
        // given
        User user = createUser();
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String refreshToken = "refreshToken";

        given(jwtService.getTokenType(anyString())).willReturn(REFRESH_TOKEN_TYPE);
        given(jwtService.getAuthentication(anyString())).willReturn(new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities()));
        given(redisUtils.getValue(anyString())).willReturn(Optional.of("wrong-token"));

        // then
        assertThrows(RefreshTokenDifferentException.class, () -> authService.reissue("Bearer " + refreshToken, response));
    }

    @Test
    @DisplayName("유저 로그아웃")
    void logout() throws Exception {
        // given
        User user = createUser();
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String accessToken = "accessToken";

        given(jwtService.getTokenType(anyString())).willReturn(ACCESS_TOKEN_TYPE);
        given(jwtService.getAuthentication(anyString())).willReturn(new UsernamePasswordAuthenticationToken(userPrincipal, "", userPrincipal.getAuthorities()));
        given(jwtService.getExpirationTime(anyString())).willReturn(3600L);

        willDoNothing().given(redisUtils).deleteData(anyString());
        willDoNothing().given(redisUtils).setValue(anyString(), anyString(), any(Duration.class));

        // then
        assertDoesNotThrow(() -> authService.logout("Bearer " + accessToken));
    }

    @Test
    @DisplayName("유저 로그아웃 시 토큰 타입 불일치")
    void logoutThrowTokenTypeException() throws Exception {
        // given
        String accessToken = "accessToken";
        given(jwtService.getTokenType(anyString())).willReturn("wrong-type");

        // then
        assertThrows(TokenTypeNotValidException.class, () -> authService.logout("Bearer " + accessToken));
    }

    private User createUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return User.builder()
                .email("test@gmail.com")
                .password(encoder.encode("123"))
                .name("tester")
                .role(Role.USER)
                .build();
    }

    private TokenResponseDto createTokenDto() {
        return TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken("atk")
                .refreshToken("rtk")
                .refreshTokenValidTime(3600L)
                .build();
    }
}