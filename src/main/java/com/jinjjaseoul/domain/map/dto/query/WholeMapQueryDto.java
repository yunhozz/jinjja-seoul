package com.jinjjaseoul.domain.map.dto.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WholeMapQueryDto {

    // map
    private Long id;
    private String name;
    private String mapIconImgUrl;
    @JsonIgnore
    private String dtype; // TM, CM

    // theme map
    private Integer curatorNum;

    // curation map
    private List<CurationMapExtraQueryDto> userInfo;
    private Integer locationNum;

    @QueryProjection
    public WholeMapQueryDto(Long id, String name, String mapIconImgUrl, String dtype) {
        this.id = id;
        this.name = name;
        this.mapIconImgUrl = mapIconImgUrl;
        this.dtype = dtype;
    }

    public void setCuratorNum(Integer curatorNum) {
        this.curatorNum = curatorNum;
    }

    public void setUserInfo(List<CurationMapExtraQueryDto> userInfo) {
        this.userInfo = userInfo;
    }

    public void setLocationNum(Integer locationNum) {
        this.locationNum = locationNum;
    }
}