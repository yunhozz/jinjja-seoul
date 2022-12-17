package com.jinjjaseoul.domain.map.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class CurationMapNameDuplicateException extends JinjjaSeoulException {

    public CurationMapNameDuplicateException() {
        super(ErrorCode.CURATION_MAP_NAME_DUPLICATE);
    }
}