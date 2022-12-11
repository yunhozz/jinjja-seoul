package com.jinjjaseoul.domain.map.dto.request;

import com.jinjjaseoul.common.enums.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@NoArgsConstructor
public class ThemeMapRequestDto {

    @NotBlank
    private String name;
    @NotEmpty
    private List<Category> categories;
    private String keyword;

    // 생성 후 장소 등록
    private Long locationId;
    private String imageUrl;
}