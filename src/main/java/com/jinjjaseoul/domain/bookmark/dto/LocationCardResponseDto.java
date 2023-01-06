package com.jinjjaseoul.domain.bookmark.dto;

import com.jinjjaseoul.domain.location.model.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationCardResponseDto {

    private Long locationId;
    private String name;
    private String si;
    private String gu;
    private String dong;
    private String etc;
    private String nx;
    private String ny;

    public LocationCardResponseDto(Location location) {
        locationId = location.getId();
        name = location.getName();
        si = location.getAddress().getSi();
        gu = location.getAddress().getGu();
        dong = location.getAddress().getDong();
        etc = location.getAddress().getEtc();
        nx = location.getNx();
        ny = location.getNy();
    }
}