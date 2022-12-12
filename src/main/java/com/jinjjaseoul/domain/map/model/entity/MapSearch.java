package com.jinjjaseoul.domain.map.model.entity;

import com.jinjjaseoul.common.enums.Beverage;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.enums.Characteristics;
import com.jinjjaseoul.common.enums.Food;
import com.jinjjaseoul.common.enums.Place;
import com.jinjjaseoul.common.enums.Somebody;
import com.jinjjaseoul.common.enums.Something;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MapSearch {

    @Enumerated(EnumType.STRING)
    private Place place;

    @Enumerated(EnumType.STRING)
    private Somebody somebody;

    @Enumerated(EnumType.STRING)
    private Something something;

    @Enumerated(EnumType.STRING)
    private Characteristics characteristics;

    @Enumerated(EnumType.STRING)
    private Food food;

    @Enumerated(EnumType.STRING)
    private Beverage beverage;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Builder
    private MapSearch(Place place, Somebody somebody, Something something, Characteristics characteristics, Food food, Beverage beverage, Category category) {
        this.place = place;
        this.somebody = somebody;
        this.something = something;
        this.characteristics = characteristics;
        this.food = food;
        this.beverage = beverage;
        this.category = category;
    }
}