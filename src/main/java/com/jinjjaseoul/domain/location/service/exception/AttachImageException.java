package com.jinjjaseoul.domain.location.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class AttachImageException extends JinjjaSeoulException {

    public AttachImageException() {
        super(ErrorCode.IMAGE_CANNOT_ATTACH);
    }
}