package com.jinjjaseoul.domain.user.service;

import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.user.dto.request.UpdateRequestDto;
import com.jinjjaseoul.domain.user.dto.request.UserRequestDto;
import com.jinjjaseoul.domain.user.dto.response.UserResponseDto;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import com.jinjjaseoul.domain.user.service.exception.EmailDuplicateException;
import com.jinjjaseoul.domain.user.service.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    IconRepository iconRepository;

    @Test
    @DisplayName("유저 회원가입")
    void join() throws Exception {
        // given
        UserRequestDto userRequestDto = new UserRequestDto("test@gmail.com", "123", "tester");
        Icon icon = createIcon();
        User user = createUser(icon);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(iconRepository.getReferenceById(anyLong())).willReturn(icon);
        given(userRepository.save(any(User.class))).willReturn(user);

        // then
        assertDoesNotThrow(() -> userService.join(userRequestDto));
    }

    @Test
    @DisplayName("유저 회원가입 시 이메일 중복")
    void joinThrowException() throws Exception {
        // given
        UserRequestDto userRequestDto = new UserRequestDto("test@gmail.com", "123", "tester");
        Icon icon = createIcon();
        User user = createUser(icon);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // then
        assertThrows(EmailDuplicateException.class, () -> userService.join(userRequestDto));
    }

    @Test
    @DisplayName("유저 프로필 업데이트")
    void updateProfile() throws Exception {
        // given
        Icon icon = createIcon();
        User user = createUser(icon);
        UpdateRequestDto updateRequestDto = new UpdateRequestDto("update", "this is update", 1L);

        given(userRepository.getReferenceById(anyLong())).willReturn(user);
        given(iconRepository.getReferenceById(anyLong())).willReturn(icon);

        // then
        assertDoesNotThrow(() -> userService.updateProfile(1L, updateRequestDto));
    }

    @Test
    @DisplayName("id 로 유저 dto 조회")
    void findUserDtoById() throws Exception {
        // given
        Icon icon = createIcon();
        User user = createUser(icon);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        UserResponseDto userResponseDto = userService.findUserDtoById(1L);

        // then
        assertThat(userResponseDto.getEmail()).isEqualTo("email");
        assertThat(userResponseDto.getName()).isEqualTo("tester");
    }

    @Test
    @DisplayName("id 로 유저 dto 조회 실패")
    void findUserDtoByIdThrowException() throws Exception {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // then
        assertThrows(UserNotFoundException.class, () -> userService.findUserDtoById(100L));
    }

    private User createUser(Icon icon) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return User.builder()
                .email("email")
                .password(encoder.encode("123"))
                .name("tester")
                .icon(icon)
                .build();
    }

    private Icon createIcon() {
        return new Icon("test.ico", "test");
    }
}