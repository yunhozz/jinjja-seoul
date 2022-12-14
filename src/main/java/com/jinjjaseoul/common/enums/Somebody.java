package com.jinjjaseoul.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

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

    @JsonCreator
    public static Somebody forValue(String value) {
        return Arrays.stream(Somebody.values())
                .filter(somebody -> somebody.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}