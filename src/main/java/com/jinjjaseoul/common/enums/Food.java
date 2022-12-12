package com.jinjjaseoul.common.enums;

import lombok.Getter;

@Getter
public enum Food {

    WORLD("세계음식"),
    NOODLE("면요리"),
    MEET("고기"),
    DESERT("디저트"),
    KOREAN("한국음식"),
    VEGETARIAN("채식/비건"),
    SIMPLE("간단한음식"),
    FISH("생선"),
    SCHOOL("분식"),
    HAND("수제"),
    SPICY("매운음식"),

    ;

    private final String desc;

    Food(String desc) {
        this.desc = desc;
    }
}