package com.jinjjaseoul.domain.map.dto.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CurationMapExtraQueryDto {

    @JsonIgnore
    private Long id;
    private String userName;
    private String userIconImgUrl;

    @QueryProjection
    public CurationMapExtraQueryDto(Long id, String userName, String userIconImgUrl) {
        this.id = id;
        this.userName = userName;
        this.userIconImgUrl = userIconImgUrl;
    }
}