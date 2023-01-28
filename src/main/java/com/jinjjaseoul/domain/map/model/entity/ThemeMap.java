package com.jinjjaseoul.domain.map.model.entity;

import com.jinjjaseoul.common.enums.Beverage;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.enums.Characteristics;
import com.jinjjaseoul.common.enums.Food;
import com.jinjjaseoul.common.enums.Place;
import com.jinjjaseoul.common.enums.Somebody;
import com.jinjjaseoul.common.enums.Something;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.user.model.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DiscriminatorValue("TM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeMap extends Map {

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Category> categories = new ArrayList<>();

    @ElementCollection
    private List<String> keywordList = new ArrayList<>();

    @Builder
    private ThemeMap(User user, String name, Icon icon, List<Category> categories, List<String> keywordList) {
        super(user, name, icon);
        this.categories = categories;
        this.keywordList = keywordList;
    }

    public boolean isMadeByUser(User user) {
        return super.getUser().equals(user);
    }

    public void updateSearchCondition(Place place, Somebody somebody, Something something, Characteristics characteristics, Food food, Beverage beverage) {
        MapSearch mapSearch = createMapSearch(place, somebody, something, characteristics, food, beverage);
        super.updateMapSearch(mapSearch);
    }

    private MapSearch createMapSearch(Place place, Somebody somebody, Something something, Characteristics characteristics, Food food, Beverage beverage) {
        return MapSearch.builder()
                .place(place)
                .somebody(somebody)
                .something(something)
                .characteristics(characteristics)
                .food(food)
                .beverage(beverage)
                .build();
    }
}