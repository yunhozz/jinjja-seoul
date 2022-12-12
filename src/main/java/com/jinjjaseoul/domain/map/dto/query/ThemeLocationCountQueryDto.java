package com.jinjjaseoul.domain.map.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ThemeLocationCountQueryDto {

    private Long id;
    private Long themeMapId;

    @QueryProjection
    public ThemeLocationCountQueryDto(Long id, Long themeMapId) {
        this.id = id;
        this.themeMapId = themeMapId;
    }
}