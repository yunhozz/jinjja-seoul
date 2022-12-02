package com.jinjjaseoul.common.enums;

import lombok.Getter;

@Getter
public enum Provider {

    GOOGLE("구글"),
    KAKAO("카카오"),
    APPLE("애플")

    ;

    private final String desc;

    Provider(String desc) {
        this.desc = desc;
    }
}