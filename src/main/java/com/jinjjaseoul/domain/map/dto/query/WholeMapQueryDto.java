package com.jinjjaseoul.domain.map.dto.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WholeMapQueryDto {

    // map
    private Long id;
    private String name;
    private String mapIconImgUrl;
    private String dtype; // TM, CM

    // theme map
    private Integer curatorNum;

    // curation map
    private String userName;
    private String userIconImgUrl;
    private Integer locationNum;

    @QueryProjection
    public WholeMapQueryDto(Long id, String name, String mapIconImgUrl, String dtype, String userName, String userIconImgUrl) {
        this.id = id;
        this.name = name;
        this.mapIconImgUrl = mapIconImgUrl;
        this.dtype = dtype;
        this.userName = userName;
        this.userIconImgUrl = userIconImgUrl;
    }

    public void setCuratorNum(Integer curatorNum) {
        this.curatorNum = curatorNum;
    }

    public void setLocationNum(Integer locationNum) {
        this.locationNum = locationNum;
    }
}