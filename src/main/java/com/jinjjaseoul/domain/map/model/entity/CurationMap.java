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
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("CM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationMap extends Map {

    private boolean isMakeTogether;

    private boolean isProfileDisplay;

    private boolean isShared;

    private String redirectUrl;

    private int numOfLikes;

    @Builder
    private CurationMap(User user, String name, Icon icon, boolean isMakeTogether, boolean isProfileDisplay, boolean isShared) {
        super(user, name, icon);
        this.isMakeTogether = isMakeTogether;
        this.isProfileDisplay = isProfileDisplay;
        this.isShared = isShared;
    }

    public void updateRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public void updateSearchCondition(Place place, Somebody somebody, Something something, Characteristics characteristics, Food food, Beverage beverage, Category category) {
        MapSearch mapSearch = createMapSearch(place, somebody, something, characteristics, food, beverage, category);
        super.updateMapSearch(mapSearch);
    }

    public void addLikes() {
        numOfLikes++;
    }

    public void subtractLikes() {
        if (numOfLikes > 0) {
            numOfLikes--;

        } else throw new IllegalStateException("좋아요 수를 더 이상 뺄 수 없습니다.");
    }

    private MapSearch createMapSearch(Place place, Somebody somebody, Something something, Characteristics characteristics, Food food, Beverage beverage, Category category) {
        return MapSearch.builder()
                .place(place)
                .somebody(somebody)
                .something(something)
                .characteristics(characteristics)
                .food(food)
                .beverage(beverage)
                .category(category)
                .build();
    }
}