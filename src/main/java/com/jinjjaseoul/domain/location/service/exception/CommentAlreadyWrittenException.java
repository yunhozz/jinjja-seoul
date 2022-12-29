package com.jinjjaseoul.domain.location.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class CommentAlreadyWrittenException extends JinjjaSeoulException {

    public CommentAlreadyWrittenException() {
        super(ErrorCode.COMMENT_ALREADY_WRITTEN);
    }
}