package com.jinjjaseoul.domain.user.dto.response;

import com.jinjjaseoul.common.enums.Provider;
import com.jinjjaseoul.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String introduction;
    private Long iconId;
    private Role role;
    private Provider provider;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}