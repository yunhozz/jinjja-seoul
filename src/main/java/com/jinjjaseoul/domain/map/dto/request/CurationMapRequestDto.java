package com.jinjjaseoul.domain.map.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class CurationMapRequestDto {

    private String name;
    private Long iconId;
    @NotNull
    private Boolean isMakeTogether;
    @NotNull
    private Boolean isProfileDisplay;
    @NotNull
    private Boolean isShared;
}