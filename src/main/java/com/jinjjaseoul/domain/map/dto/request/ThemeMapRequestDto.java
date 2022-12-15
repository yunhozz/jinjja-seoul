package com.jinjjaseoul.domain.map.dto.request;

import com.jinjjaseoul.common.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeMapRequestDto {

    private String name;
    private List<Category> categories;
    private String keywordStr; // 쉼표로 구분

    // 생성 후 장소 등록
    private Long locationId;
    private String imageUrl;
}