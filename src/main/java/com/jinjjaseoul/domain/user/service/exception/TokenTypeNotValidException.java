package com.jinjjaseoul.domain.user.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class TokenTypeNotValidException extends JinjjaSeoulException {

    public TokenTypeNotValidException() {
        super(ErrorCode.TOKEN_TYPE_NOT_VALID);
    }
}