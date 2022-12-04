package com.jinjjaseoul.domain.user.service;

import com.jinjjaseoul.common.converter.UserConverter;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.user.dto.UpdateRequestDto;
import com.jinjjaseoul.domain.user.dto.UserRequestDto;
import com.jinjjaseoul.domain.user.model.entity.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import com.jinjjaseoul.domain.user.service.exception.EmailDuplicateException;
import com.jinjjaseoul.domain.user.service.exception.IconNotFoundException;
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
    public void updateProfile(Long userId, UpdateRequestDto updateRequestDto) {
        User user = userRepository.getReferenceById(userId);
        Icon icon = iconRepository.findById(updateRequestDto.getIconId())
                .orElseThrow(IconNotFoundException::new);

        user.updateProfile(updateRequestDto.getName(), updateRequestDto.getIntroduction(), icon);
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