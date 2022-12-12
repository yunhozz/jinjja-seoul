package com.jinjjaseoul.domain.map.model.entity;

import com.jinjjaseoul.common.enums.Beverage;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.enums.Characteristics;
import com.jinjjaseoul.common.enums.Food;
import com.jinjjaseoul.common.enums.Place;
import com.jinjjaseoul.common.enums.Somebody;
import com.jinjjaseoul.common.enums.Something;
import com.jinjjaseoul.domain.BaseEntity;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.user.model.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationMap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private Icon icon;

    private boolean isMakeTogether;

    private boolean isProfileDisplay;

    private boolean isShared;

    private String redirectUrl;

    private int numOfLikes;

    @Embedded
    private MapSearch mapSearch; // 검색 조건 : 어디로?, 누구와?, 무엇을?, 분위기/특징, 음식, 술/음료, 카테고리

    @Builder
    private CurationMap(User user, String name, Icon icon, boolean isMakeTogether, boolean isProfileDisplay, boolean isShared, String redirectUrl) {
        this.user = user;
        this.name = name;
        this.icon = icon;
        this.isMakeTogether = isMakeTogether;
        this.isProfileDisplay = isProfileDisplay;
        this.isShared = isShared;
        this.redirectUrl = redirectUrl;
    }

    public void updateSearchCondition(Place place, Somebody somebody, Something something, Characteristics characteristics, Food food, Beverage beverage, Category category) {
        mapSearch = MapSearch.builder()
                .place(place)
                .somebody(somebody)
                .something(something)
                .characteristics(characteristics)
                .food(food)
                .beverage(beverage)
                .category(category)
                .build();
    }

    public void addLikes() {
        numOfLikes++;
    }

    public void subtractLikes() {
        if (numOfLikes > 0) {
            numOfLikes--;

        } else throw new IllegalStateException("좋아요 수를 더 이상 뺄 수 없습니다.");
    }
}