package com.jinjjaseoul.domain.user.service;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.converter.UserConverter;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.user.dto.ProfileResponseDto;
import com.jinjjaseoul.domain.user.dto.UpdateRequestDto;
import com.jinjjaseoul.domain.user.dto.UserRequestDto;
import com.jinjjaseoul.domain.user.dto.UserResponseDto;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.UserRepository;
import com.jinjjaseoul.domain.user.service.exception.EmailDuplicateException;
import com.jinjjaseoul.domain.user.service.exception.IconNotFoundException;
import com.jinjjaseoul.domain.user.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void updateProfile(UserPrincipal userPrincipal, UpdateRequestDto updateRequestDto) {
        User user = userRepository.getReferenceById(userPrincipal.getId());
        Icon icon = iconRepository.findById(updateRequestDto.getIconId())
                .orElseThrow(IconNotFoundException::new);

        user.updateProfile(updateRequestDto.getName(), updateRequestDto.getIntroduction(), icon);
    }

    @Transactional(readOnly = true)
    public ProfileResponseDto findProfileDto(UserPrincipal userPrincipal) {
        User user = userRepository.findWithIconById(userPrincipal.getId())
                .orElseThrow(UserNotFoundException::new);
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
            Icon testIcon = iconRepository.getReferenceById(1L); // 테스트를 위한 임시 아이콘 생성 -> 랜덤 id
            users[0] = UserConverter.convertToEntity(userRequestDto, testIcon);
        });

        return users[0];
    }
}