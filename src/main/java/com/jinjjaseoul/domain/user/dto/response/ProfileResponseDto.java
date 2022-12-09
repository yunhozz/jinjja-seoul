package com.jinjjaseoul.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {

    private String name;
    private String introduction;
    private Long iconId;
}