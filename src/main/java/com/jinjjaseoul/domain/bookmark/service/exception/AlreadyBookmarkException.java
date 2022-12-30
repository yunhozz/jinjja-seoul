package com.jinjjaseoul.domain.bookmark.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class AlreadyBookmarkException extends JinjjaSeoulException {

    public AlreadyBookmarkException() {
        super(ErrorCode.ALREADY_BOOKMARK);
    }
}