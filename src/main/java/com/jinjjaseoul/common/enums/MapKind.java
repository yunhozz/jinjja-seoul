package com.jinjjaseoul.common.enums;

import lombok.Getter;

@Getter
public enum MapKind {

    THEME("테마지도"),
    CURATION("큐레이션지도"),

    ;

    private final String desc;

    MapKind(String desc) {
        this.desc = desc;
    }
}