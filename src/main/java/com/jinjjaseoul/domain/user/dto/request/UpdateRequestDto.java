package com.jinjjaseoul.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class UpdateRequestDto {

    @NotBlank
    private String name;

    private String introduction;

    @NotNull
    private Long iconId;
}