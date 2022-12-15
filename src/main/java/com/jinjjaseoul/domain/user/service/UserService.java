package com.jinjjaseoul.domain.user.service;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.converter.UserConverter;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.user.dto.request.UpdateRequestDto;
import com.jinjjaseoul.domain.user.dto.request.UserRequestDto;
import com.jinjjaseoul.domain.user.dto.response.ProfileResponseDto;
import com.jinjjaseoul.domain.user.dto.response.UserResponseDto;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.UserRepository;
import com.jinjjaseoul.domain.user.service.exception.EmailDuplicateException;
import lombok.RequiredArgsConstructor;
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
    public ProfileResponseDto findProfileDto(UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        UserResponseDto userResponseDto = UserConverter.convertToDto(user);

        return new ProfileResponseDto(userResponseDto.getName(), userResponseDto.getIntroduction(), userResponseDto.getIconId());
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
            users[0] = UserConverter.convertToEntity(userRequestDto, icon);
        });

        return users[0];
    }

    private Icon randomIcon() {
        Random random = new Random(System.currentTimeMillis());
        return iconRepository.getReferenceById((long) random.nextInt(8) + 1);
    }
}