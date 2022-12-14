package com.jinjjaseoul.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Place {

    ULJIRO("을지로/충무로"),
    GWANGWHAMUN("광화문/시청"),
    SAMSUNG("삼성/역삼/선릉"),
    SEONGSU("성수"),
    JONGNO("종로/중구"),
    SONGPA("송파/강동"),
    HONGDAE("홍대/합정"),
    SEOUNGBUK("성북"),
    YOUNGDEOUNGPO("영등포/금천"),
    ITAEWON("이태원/경리단"),
    GANGNAM("강남/서초/방배"),
    EUNPEOUNG("은평"),
    MANGWON("망원"),
    DONGDAEMUN("동대문/성동"),
    APGUJUNG("압구정/신사"),
    SINCHON("신촌"),
    GWANAK("관악/동작"),
    YOUNGSAN("용산/마포"),
    DAEHAKNO("대학로/혜화"),
    GANGSEO("강서"),
    NOWON("노원/도봉/강북"),
    SEOCHON("서촌/북촌"),
    YEONNAM("연남/연희"),
    GURO("구로"),
    YEOEDO("여의도"),

    ;

    private final String desc;

    Place(String desc) {
        this.desc = desc;
    }

    @JsonCreator
    public static Place forValue(String value) {
        return Arrays.stream(Place.values())
                .filter(place -> place.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}