package com.jinjjaseoul.domain.map.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ThemeLocationSimpleQueryDto {

    // theme location
    private Long id;

    // location
    private Long locationId;
    private String name;

    // icon
    private Long iconId;
    private String imageUrl;

    @QueryProjection
    public ThemeLocationSimpleQueryDto(Long id, Long locationId, String name, Long iconId, String imageUrl) {
        this.id = id;
        this.locationId = locationId;
        this.name = name;
        this.iconId = iconId;
        this.imageUrl = imageUrl;
    }
}