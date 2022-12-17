package com.jinjjaseoul.domain.map.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CurationMapQueryDto {

    // curation map
    private Long id;
    private String name;

    // curation map icon
    private String curationMapImageUrl;

    // user
    private String userName;

    // user icon
    private String userImageUrl;

    private int locationNum;

    @QueryProjection
    public CurationMapQueryDto(Long id, String name, String curationMapImageUrl, String userName, String userImageUrl) {
        this.id = id;
        this.name = name;
        this.curationMapImageUrl = curationMapImageUrl;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
    }

    public void setLocationNum(int locationNum) {
        this.locationNum = locationNum;
    }
}