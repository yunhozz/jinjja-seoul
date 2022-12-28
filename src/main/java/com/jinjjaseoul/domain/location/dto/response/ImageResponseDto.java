package com.jinjjaseoul.domain.location.dto.response;

import com.jinjjaseoul.domain.location.model.entity.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ImageResponseDto {

    private Long id;
    private String originalName;
    private String savedName;
    private String savedPath;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public ImageResponseDto(Image image) {
        id = image.getId();
        originalName = image.getOriginalName();
        savedName = image.getSavedName();
        savedPath = image.getSavedPath();
        createdDate = image.getCreatedDate();
        lastModifiedDate = image.getLastModifiedDate();
    }
}