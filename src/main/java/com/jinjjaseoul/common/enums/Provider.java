package com.jinjjaseoul.common.enums;

import lombok.Getter;

@Getter
public enum Provider {

    LOCAL("로컬"),
    GOOGLE("구글"),
    KAKAO("카카오"),
    NAVER("네이버")

    ;

    private final String desc;

    Provider(String desc) {
        this.desc = desc;
    }
}