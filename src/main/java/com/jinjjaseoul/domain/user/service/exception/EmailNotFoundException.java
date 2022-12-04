package com.jinjjaseoul.domain.user.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class EmailNotFoundException extends JinjjaSeoulException {

    public EmailNotFoundException() {
        super(ErrorCode.EMAIL_NOT_FOUND);
    }
}