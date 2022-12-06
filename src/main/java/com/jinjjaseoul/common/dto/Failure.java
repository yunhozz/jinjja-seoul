package com.jinjjaseoul.common.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Failure<T> implements Result {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final T data;

    public Failure(T data) {
        this.data = data;
    }
}