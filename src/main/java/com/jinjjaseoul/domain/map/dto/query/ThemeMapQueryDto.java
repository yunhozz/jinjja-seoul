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
    private Long iconId;
    private String imageUrl;

    private int curatorNum;

    @QueryProjection
    public ThemeMapQueryDto(Long id, String name, Long iconId, String imageUrl) {
        this.id = id;
        this.name = name;
        this.iconId = iconId;
        this.imageUrl = imageUrl;
    }

    public void setCuratorNum(int curatorNum) {
        this.curatorNum = curatorNum;
    }
}