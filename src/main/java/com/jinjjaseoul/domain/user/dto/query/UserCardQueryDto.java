package com.jinjjaseoul.domain.user.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCardQueryDto {

    // user
    private Long id;
    private String name;
    private int numOfRecommend;

    // icon
    private String imageUrl;

    @QueryProjection
    public UserCardQueryDto(Long id, String name, int numOfRecommend, String imageUrl) {
        this.id = id;
        this.name = name;
        this.numOfRecommend = numOfRecommend;
        this.imageUrl = imageUrl;
    }
}