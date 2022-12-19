package com.jinjjaseoul.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MapKind {

    THEME("테마지도"),
    CURATION("큐레이션지도"),
    WHOLE("전체"),

    ;

    private final String desc;

    MapKind(String desc) {
        this.desc = desc;
    }

    @JsonCreator
    public static MapKind forValue(String value) {
        return Arrays.stream(MapKind.values())
                .filter(mapKind -> mapKind.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}