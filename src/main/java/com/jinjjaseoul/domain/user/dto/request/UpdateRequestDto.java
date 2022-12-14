package com.jinjjaseoul.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestDto {

    @NotBlank
    private String name;
    private String introduction;
    @NotNull
    private Long iconId;
}