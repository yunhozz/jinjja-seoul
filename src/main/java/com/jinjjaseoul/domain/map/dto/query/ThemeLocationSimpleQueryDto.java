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
    private String imageUrl;

    @QueryProjection
    public ThemeLocationSimpleQueryDto(Long id, Long locationId, String name, String imageUrl) {
        this.id = id;
        this.locationId = locationId;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}