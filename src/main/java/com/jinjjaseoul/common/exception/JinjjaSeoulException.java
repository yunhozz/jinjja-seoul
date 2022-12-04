package com.jinjjaseoul.common.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import lombok.Getter;

@Getter
public class JinjjaSeoulException extends RuntimeException {

    private final ErrorCode errorCode;

    public JinjjaSeoulException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}