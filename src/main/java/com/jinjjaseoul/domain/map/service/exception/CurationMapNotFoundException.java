package com.jinjjaseoul.domain.map.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class CurationMapNotFoundException extends JinjjaSeoulException {

    public CurationMapNotFoundException() {
        super(ErrorCode.CURATION_MAP_NOT_FOUND);
    }
}