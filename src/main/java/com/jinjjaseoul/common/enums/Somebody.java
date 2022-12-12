package com.jinjjaseoul.common.enums;

import lombok.Getter;

@Getter
public enum Somebody {

    ALONE("혼자서"),
    TOGETHER("동료랑"),
    COUPLE("연인이랑"),
    PET("반려동물과"),
    FRIEND("친구랑"),
    SMALL("소규모로"),
    BLIND_DATE("소개팅"),
    KID("아이랑"),
    PARENTS("부모님이랑"),

    ;

    private final String desc;

    Somebody(String desc) {
        this.desc = desc;
    }
}