package com.jinjjaseoul.domain.location.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class LocationQueryDto {

    // location
    private Long id;
    private String name;
    private String si;
    private String gu;
    private String dong;
    private String etc;
    private String nx;
    private String ny;

    // image list
    private List<String> imageUrlList;

    // theme map name list
    private List<ThemeMapNameQueryDto> themeMapNameList;

    // comment list
    private List<CommentQueryDto> comments;

    @QueryProjection
    public LocationQueryDto(Long id, String name, String si, String gu, String dong, String etc, String nx, String ny) {
        this.id = id;
        this.name = name;
        this.si = si;
        this.gu = gu;
        this.dong = dong;
        this.etc = etc;
        this.nx = nx;
        this.ny = ny;
    }

    public void setImageUrlList(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public void setThemeMapNameList(List<ThemeMapNameQueryDto> themeMapNameList) {
        this.themeMapNameList = themeMapNameList;
    }

    public void setComments(List<CommentQueryDto> comments) {
        this.comments = comments;
    }
}