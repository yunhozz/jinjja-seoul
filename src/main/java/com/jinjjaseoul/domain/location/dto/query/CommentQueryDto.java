package com.jinjjaseoul.domain.location.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentQueryDto {

    private String iconImgUrl;
    private String content;

    @QueryProjection
    public CommentQueryDto(String iconImgUrl, String content) {
        this.iconImgUrl = iconImgUrl;
        this.content = content;
    }
}