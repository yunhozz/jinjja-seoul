package com.jinjjaseoul.common.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ADMIN("ROLE_ADMIN", "운영자"),
    USER("ROLE_USER", "일반 사용자"),

    ;

    private final String key;
    private final String value;

    Role(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getAuthority() {
        return key;
    }

    public String getValue() {
        return value;
    }
}