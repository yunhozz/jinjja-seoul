package com.jinjjaseoul.domain.location.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class CommentRequestDto {

    @NotNull
    private Long iconId;
    @NotBlank
    private String content;
}