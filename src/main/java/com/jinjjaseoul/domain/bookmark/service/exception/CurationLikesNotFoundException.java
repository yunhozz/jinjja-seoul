package com.jinjjaseoul.domain.bookmark.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class CurationLikesNotFoundException extends JinjjaSeoulException {

    public CurationLikesNotFoundException() {
        super(ErrorCode.CURATION_LIKES_NOT_FOUND);
    }
}