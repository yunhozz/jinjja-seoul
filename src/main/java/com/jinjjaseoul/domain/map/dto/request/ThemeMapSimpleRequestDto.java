package com.jinjjaseoul.domain.map.dto.request;

import com.jinjjaseoul.common.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ThemeMapSimpleRequestDto {

    @NotBlank
    private String name;
    private Long iconId;
    @NotEmpty
    private List<Category> categories;
    private String keywordStr; // 쉼표로 구분
}