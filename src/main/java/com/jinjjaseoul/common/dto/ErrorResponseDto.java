package com.jinjjaseoul.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jinjjaseoul.common.enums.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {

    private Integer status;
    private String code;
    private String message;
    private List<NotValidResponseDto> notValidList;

    public ErrorResponseDto(ErrorCode errorCode) {
        status = errorCode.getStatus();
        code = errorCode.getCode();
        message = errorCode.getMessage();
    }

    public ErrorResponseDto(ErrorCode errorCode, List<NotValidResponseDto> notValidList) {
        status = errorCode.getStatus();
        code = errorCode.getCode();
        message = errorCode.getMessage();
        this.notValidList = notValidList;
    }
}