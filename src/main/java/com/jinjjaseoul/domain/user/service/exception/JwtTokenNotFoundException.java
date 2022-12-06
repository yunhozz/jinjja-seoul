package com.jinjjaseoul.domain.user.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class JwtTokenNotFoundException extends JinjjaSeoulException {

    public JwtTokenNotFoundException() {
        super(ErrorCode.JWT_TOKEN_NOT_FOUND);
    }
}