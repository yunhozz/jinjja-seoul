package com.jinjjaseoul.common.enums;

import lombok.Getter;

@Getter
public enum Category {

    FOOD("식당"),
    CAFE("카페"),
    BAR("주점"),
    STORE("상점"),
    WORK("작업"),
    LIFE("일상"),
    PET("반려동물"),
    CULTURE("문화공간"),
    ETC("기타"),

    ;

    private final String desc;

    Category(String desc) {
        this.desc = desc;
    }
}