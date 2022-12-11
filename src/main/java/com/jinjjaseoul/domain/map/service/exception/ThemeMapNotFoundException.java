package com.jinjjaseoul.domain.map.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class ThemeMapNotFoundException extends JinjjaSeoulException {

    public ThemeMapNotFoundException() {
        super(ErrorCode.THEME_MAP_NOT_FOUND);
    }
}