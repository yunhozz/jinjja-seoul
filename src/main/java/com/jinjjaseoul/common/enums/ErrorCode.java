package com.jinjjaseoul.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // global
    NOT_VALID(400, "G-001", "잘못된 요청입니다."),
    UNAUTHORIZED(401, "G-011", "사용자에 대한 권한이 없습니다."),
    FORBIDDEN(403, "G-031", "접근이 거부된 사용자입니다."),
    NOT_FOUND(404, "G-041", "페이지를 찾을 수 없습니다."),
    INTER_SERVER_ERROR(500, "G-051", "서버 에러가 발생했습니다."),

    // user
    EMAIL_DUPLICATED(400, "U-001", "중복되는 이메일이 존재합니다."),
    EMAIL_NOT_FOUND(400, "U-002", "해당 이메일이 존재하지 않습니다."),
    PASSWORD_DIFFERENT(400, "U-003", "비밀번호가 일치하지 않습니다."),
    ALREADY_LOGIN(400, "U-004", "이미 로그인 상태입니다."),
    NOT_REFRESH_TOKEN(400, "U-005", "재발급 토큰이 아닙니다."),
    REFRESH_TOKEN_DIFFERENT(400, "U-006", "재발급 토큰 값이 다릅니다."),
    USER_NOT_FOUND(404, "U-041", "해당 유저를 찾을 수 없습니다."),
    JWT_TOKEN_NOT_FOUND(404, "U-042", "jwt 토큰이 만료되었거나 찾을 수 없습니다."),

    // icon
    ICON_NOT_FOUND(404, "I-041", "해당 아이콘을 찾을 수 없습니다."),

    ;

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}