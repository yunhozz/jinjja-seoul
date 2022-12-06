package com.jinjjaseoul.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private Boolean success;
    private HttpStatus status;
    private Result result;

    public static Response success(HttpStatus status) {
        return new Response(true, status, null);
    }

    public static <T> Response success(HttpStatus status, T data) {
        return new Response(true, status, new Success<>(data));
    }

    public static <T> Response failure(HttpStatus status, T data) {
        return new Response(false, status, new Failure<>(data));
    }
}