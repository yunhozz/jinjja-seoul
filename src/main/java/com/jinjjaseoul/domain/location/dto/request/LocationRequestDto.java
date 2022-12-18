package com.jinjjaseoul.domain.location.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class LocationRequestDto {

    @NotBlank
    private String name;
    @NotBlank
    private String si;
    @NotBlank
    private String gu;
    @NotBlank
    private String dong;
    private String etc;
    @NotBlank
    private String nx;
    @NotBlank
    private String ny;
}