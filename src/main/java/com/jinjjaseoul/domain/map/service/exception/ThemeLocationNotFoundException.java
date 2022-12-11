package com.jinjjaseoul.domain.map.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class ThemeLocationNotFoundException extends JinjjaSeoulException {

    public ThemeLocationNotFoundException() {
        super(ErrorCode.THEME_LOCATION_NOT_FOUND);
    }
}