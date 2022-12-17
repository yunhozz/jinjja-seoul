package com.jinjjaseoul.domain.map.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CurationLocationCountQueryDto {

    private Long id;
    private Long curationMapId;

    @QueryProjection
    public CurationLocationCountQueryDto(Long id, Long curationMapId) {
        this.id = id;
        this.curationMapId = curationMapId;
    }
}