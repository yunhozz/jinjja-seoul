package com.jinjjaseoul.domain.user.service.exception;

import com.jinjjaseoul.common.exception.JinjjaSeoulException;
import com.jinjjaseoul.common.enums.ErrorCode;

public class UserNotFoundException extends JinjjaSeoulException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}