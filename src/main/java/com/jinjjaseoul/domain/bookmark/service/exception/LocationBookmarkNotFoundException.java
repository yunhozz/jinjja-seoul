package com.jinjjaseoul.domain.bookmark.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class LocationBookmarkNotFoundException extends JinjjaSeoulException {

    public LocationBookmarkNotFoundException() {
        super(ErrorCode.LOCATION_BOOKMARK_NOT_FOUND);
    }
}