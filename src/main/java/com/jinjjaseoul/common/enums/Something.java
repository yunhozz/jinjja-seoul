package com.jinjjaseoul.common.enums;

import lombok.Getter;

@Getter
public enum Something {

    LUNCH("점심식사"),
    WORK("일하기"),
    MUSIC("음악듣기"),
    BOOK("책읽기"),
    WALK("산책"),
    SUMMER("한여름에"),
    STUDY("공부하기"),
    EXERCISE("운동하기"),
    SPECIAL("특별한날"),
    INSPIRATION("영감얻기"),
    MOVIE("영화보기"),
    TALK("대화하기"),
    LATE("늦게까지"),
    SPACE_OUT("멍때리기"),
    PICTURE("사진찍기"),
    CAR("차끌고"),
    RAIN("비오는날"),
    DINNER("저녁식사"),
    RECEPTION("대접하기"),

    ;

    private final String desc;

    Something(String desc) {
        this.desc = desc;
    }
}