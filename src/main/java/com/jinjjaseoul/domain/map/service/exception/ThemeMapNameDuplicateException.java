package com.jinjjaseoul.domain.map.service.exception;

import com.jinjjaseoul.common.enums.ErrorCode;
import com.jinjjaseoul.common.exception.JinjjaSeoulException;

public class ThemeMapNameDuplicateException extends JinjjaSeoulException {

    public ThemeMapNameDuplicateException() {
        super(ErrorCode.THEME_MAP_NAME_DUPLICATE);
    }
}