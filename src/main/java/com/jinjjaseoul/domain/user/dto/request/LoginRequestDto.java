package com.jinjjaseoul.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}