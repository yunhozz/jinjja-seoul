package com.jinjjaseoul.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Characteristics {

    COST("가성비"),
    LOCAL("현지같은"),
    GREEN("그린에코"),
    HEALTH("건강한"),
    SCENERY("경치좋은"),
    HIDDEN("숨겨진"),
    SKILLFUL("실력있는"),
    COMFORTABLE("편안한"),
    PLENTIFUL("푸짐한"),
    VINTAGE("빈티지"),
    HIP("힙한"),
    CLEAN("깔끔한"),
    EXPENSIVE("비싼"),
    ROOFTOP("루프탑/테라스"),
    UNIQUE("개성있는"),
    KIND("친절한"),
    INSTA("인스타감성"),
    HONEST("정직한"),
    QUIET("조용한"),
    NOT_CROWDED("붐비지않는"),
    SMOKE("흡연가능"),
    CASUAL("캐주얼한"),
    GENDER("성평등한"),
    SUNNY("햇빛좋은"),
    OLD("오래된"),
    HIGH("높은층고"),
    PARKING("주차편한"),

    ;

    private final String desc;

    Characteristics(String desc) {
        this.desc = desc;
    }

    @JsonCreator
    public static Characteristics forValue(String value) {
        return Arrays.stream(Characteristics.values())
                .filter(somebody -> somebody.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}