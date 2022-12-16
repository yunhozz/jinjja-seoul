package com.jinjjaseoul.domain.user.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileQueryDto {

    private Long id;
    private String name;
    private String introduction;
    private String imageUrl; // icon

    @QueryProjection
    public ProfileQueryDto(Long id, String name, String introduction, String imageUrl) {
        this.id = id;
        this.name = name;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
    }
}