package com.jinjjaseoul.domain.user.service;

import com.jinjjaseoul.common.enums.Provider;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.user.dto.request.UpdateRequestDto;
import com.jinjjaseoul.domain.user.dto.request.UserRequestDto;
import com.jinjjaseoul.domain.user.dto.response.UserResponseDto;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import com.jinjjaseoul.domain.user.service.exception.EmailDuplicateException;
import com.jinjjaseoul.domain.user.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final IconRepository iconRepository;

    @Transactional
    public Long join(UserRequestDto userRequestDto) {
        User user = validateAndSaveUser(userRequestDto);
        return userRepository.save(user).getId();
    }

    @Transactional
    public void updateProfile(Long userId, UpdateRequestDto updateRequestDto) {
        User user = userRepository.getReferenceById(userId);
        Icon icon = iconRepository.getReferenceById(updateRequestDto.getIconId());
        user.updateProfile(updateRequestDto.getName(), updateRequestDto.getIntroduction(), icon);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findUserDtoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return new UserResponseDto(user);
    }

    private User validateAndSaveUser(UserRequestDto userRequestDto) {
        final User[] users = {null};
        userRepository.findByEmail(userRequestDto.getEmail()).ifPresentOrElse(user -> {
            if (user.isDeleted()) {
                user.reAssign();
                users[0] = user;

            } else throw new EmailDuplicateException();

        }, () -> {
            Icon icon = randomIcon();
            users[0] = createUser(userRequestDto, icon);
        });

        return users[0];
    }

    private User createUser(UserRequestDto userRequestDto, Icon icon) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return User.builder()
                .email(userRequestDto.getEmail())
                .password(encoder.encode(userRequestDto.getPassword()))
                .name(userRequestDto.getName())
                .introduction(null)
                .icon(icon)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();
    }

    private Icon randomIcon() {
        Random random = new Random(System.currentTimeMillis());
        return iconRepository.getReferenceById((long) random.nextInt(8) + 1);
    }
}