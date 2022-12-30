package com.jinjjaseoul.common.exception;

import com.jinjjaseoul.common.dto.ErrorResponseDto;
import com.jinjjaseoul.common.dto.NotValidResponseDto;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.common.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Response handleRuntimeException(RuntimeException e) {
        // 접근이 거부된 사용자가 접근할 때 예외 발생
        if (e instanceof AccessDeniedException) {
            return failure(e, ErrorCode.FORBIDDEN);
        }
        // 권한이 없는 사용자가 요청할 때 예외 발생
        if (e instanceof AuthenticationException) {
            return failure(e, ErrorCode.UNAUTHORIZED);
        }
        // 런타임 예외 발생
        return failure(e, ErrorCode.INTER_SERVER_ERROR);
    }

    // 커스텀 예외 발생
    @ExceptionHandler(JinjjaSeoulException.class)
    public Response handleJinjjaSeoulException(JinjjaSeoulException e) {
        return failure(e, e.getErrorCode());
    }

    // @Valid 를 통과하지 못하면 예외 발생
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleRuntimeException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<NotValidResponseDto> notValidResponseDtoList = new ArrayList<>() {{
            for (FieldError fieldError : fieldErrors) {
                NotValidResponseDto notValidResponseDto = NotValidResponseDto.builder()
                        .field(fieldError.getField())
                        .rejectedValue(fieldError.getRejectedValue())
                        .message(fieldError.getDefaultMessage())
                        .build();
                add(notValidResponseDto);
            }
        }};

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(ErrorCode.NOT_VALID, notValidResponseDtoList);
        return Response.failure(HttpStatus.valueOf(errorResponseDto.getStatus()), errorResponseDto);
    }

    // getReferenceById 로 엔티티 프록시 조회에 실패했을 때 예외 발생
    @ExceptionHandler(EntityNotFoundException.class)
    public Response handleEntityNotFoundException(EntityNotFoundException e) {
        return failure(e, ErrorCode.NOT_FOUND);
    }

    private Response failure(Exception e, ErrorCode errorCode) {
        log.error(String.format("handle%s", e.getClass().getSimpleName()));
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errorCode);
        return Response.failure(HttpStatus.valueOf(errorCode.getStatus()), errorResponseDto);
    }
}