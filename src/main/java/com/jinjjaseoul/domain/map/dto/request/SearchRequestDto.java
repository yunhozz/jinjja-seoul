package com.jinjjaseoul.domain.map.dto.request;

import com.jinjjaseoul.common.enums.Beverage;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.enums.Characteristics;
import com.jinjjaseoul.common.enums.Food;
import com.jinjjaseoul.common.enums.Place;
import com.jinjjaseoul.common.enums.Somebody;
import com.jinjjaseoul.common.enums.Something;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {

    private String keyword;
    private Place place;
    private Somebody somebody;
    private Something something;
    private Characteristics characteristics;
    private Food food;
    private Beverage beverage;
    private List<Category> categories;
}