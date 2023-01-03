package com.jinjjaseoul.domain.user.dto.response;

import com.jinjjaseoul.common.enums.Provider;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String introduction;
    private Role role;
    private Provider provider;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public UserResponseDto(User user) {
        id = user.getId();
        email = user.getEmail();
        password = user.getPassword();
        name = user.getName();
        introduction = user.getIntroduction();
        role = user.getRole();
        provider = user.getProvider();
        createdDate = user.getCreatedDate();
        lastModifiedDate = user.getLastModifiedDate();
    }
}