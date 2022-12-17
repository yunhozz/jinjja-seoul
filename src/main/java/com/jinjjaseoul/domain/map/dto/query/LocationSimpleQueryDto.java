package com.jinjjaseoul.domain.map.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LocationSimpleQueryDto {

    // location
    private Long id;
    private String locationName;
    private String si;
    private String gu;
    private String dong;
    private String etc;

    // user
    private Long userId;
    private String name;
    private String introduction;

    // icon
    private String imageUrl;

    // comment
    private String largestComment;

    @QueryProjection
    public LocationSimpleQueryDto(Long id, String locationName, String si, String gu, String dong, String etc, Long userId, String name, String introduction, String imageUrl) {
        this.id = id;
        this.locationName = locationName;
        this.si = si;
        this.gu = gu;
        this.dong = dong;
        this.etc = etc;
        this.userId = userId;
        this.name = name;
        this.introduction = introduction;
        this.imageUrl = imageUrl;
    }

    public void setLargestComment(String largestComment) {
        this.largestComment = largestComment;
    }
}