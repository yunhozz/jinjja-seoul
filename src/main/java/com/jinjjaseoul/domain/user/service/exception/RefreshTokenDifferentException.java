package com.jinjjaseoul.domain.user.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class RefreshTokenDifferentException extends JinjjaSeoulException {

    public RefreshTokenDifferentException() {
        super(ErrorCode.REFRESH_TOKEN_DIFFERENT);
    }
}