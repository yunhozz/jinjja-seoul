package com.jinjjaseoul.domain.location.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private String si; // 시
    private String gu; // 구
    private String dong; // 동
    private String etc;

    public Address(String si, String gu, String dong, String etc) {
        this.si = si;
        this.gu = gu;
        this.dong = dong;
        this.etc = etc;
    }
}