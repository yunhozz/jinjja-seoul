package com.jinjjaseoul.domain.user.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class PasswordDifferentException extends JinjjaSeoulException {

    public PasswordDifferentException() {
        super(ErrorCode.PASSWORD_DIFFERENT);
    }
}