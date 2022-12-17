package com.jinjjaseoul.domain.map.dto.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentSimpleQueryDto {

    // comment
    private Long id;
    private String content;

    // location
    @JsonIgnore
    private Long locationId;

    @QueryProjection
    public CommentSimpleQueryDto(Long id, String content, Long locationId) {
        this.id = id;
        this.content = content;
        this.locationId = locationId;
    }
}