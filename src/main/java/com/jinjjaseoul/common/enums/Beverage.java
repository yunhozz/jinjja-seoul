package com.jinjjaseoul.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Beverage {

    COFFEE("커피"),
    WINE("와인"),
    BEER("맥주"),
    TEA("차"),
    TRADITIONAL("전통주"),
    COCKTAIL("칵테일"),
    HIGHBALL("하이볼"),
    SOJU("소주"),
    WHISKY("위스키"),
    SAKE("사케"),

    ;

    private final String desc;

    Beverage(String desc) {
        this.desc = desc;
    }

    @JsonCreator
    public static Beverage forValue(String value) {
        return Arrays.stream(Beverage.values())
                .filter(somebody -> somebody.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}