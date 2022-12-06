package com.jinjjaseoul.domain.user.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class NotRefreshTokenException extends JinjjaSeoulException {

    public NotRefreshTokenException() {
        super(ErrorCode.NOT_REFRESH_TOKEN);
    }
}