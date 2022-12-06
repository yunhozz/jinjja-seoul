package com.jinjjaseoul.domain.user.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class EmailDuplicateException extends JinjjaSeoulException {

    public EmailDuplicateException() {
        super(ErrorCode.EMAIL_DUPLICATED);
    }
}