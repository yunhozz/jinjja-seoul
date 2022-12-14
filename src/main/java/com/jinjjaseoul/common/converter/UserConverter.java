package com.jinjjaseoul.common.converter;

import com.jinjjaseoul.common.enums.Provider;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.user.dto.request.UserRequestDto;
import com.jinjjaseoul.domain.user.dto.response.UserResponseDto;
import com.jinjjaseoul.domain.user.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserConverter {

    public static User convertToEntity(UserRequestDto userRequestDto, Icon icon) {
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

    public static UserResponseDto convertToDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .introduction(user.getIntroduction())
                .iconId(user.getIcon().getId())
                .role(user.getRole())
                .provider(user.getProvider())
                .createdDate(user.getCreatedDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .build();
    }
}