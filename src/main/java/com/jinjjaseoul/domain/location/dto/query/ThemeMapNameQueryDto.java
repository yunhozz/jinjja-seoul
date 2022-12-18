package com.jinjjaseoul.domain.location.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ThemeMapNameQueryDto {

    private String name;
    private String iconImgUrl;

    @QueryProjection
    public ThemeMapNameQueryDto(String name, String iconImgUrl) {
        this.name = name;
        this.iconImgUrl = iconImgUrl;
    }
}