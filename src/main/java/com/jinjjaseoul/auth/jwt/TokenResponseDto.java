package com.jinjjaseoul.auth.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenValidTime;
}