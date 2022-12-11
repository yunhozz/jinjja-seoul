package com.jinjjaseoul.domain.map.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class CurationLocationNotFoundException extends JinjjaSeoulException {

    public CurationLocationNotFoundException() {
        super(ErrorCode.CURATION_LOCATION_NOT_FOUND);
    }
}