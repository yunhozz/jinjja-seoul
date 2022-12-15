package com.jinjjaseoul.domain.map.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ThemeMapQueryDto {

    // theme map
    private Long id;
    private String name;

    // icon
    private String imageUrl;

    private int curatorNum;

    @QueryProjection
    public ThemeMapQueryDto(Long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void setCuratorNum(int curatorNum) {
        this.curatorNum = curatorNum;
    }
}