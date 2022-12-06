package com.jinjjaseoul.domain.user.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class AlreadyLoginException extends JinjjaSeoulException {

    public AlreadyLoginException() {
        super(ErrorCode.ALREADY_LOGIN);
    }
}