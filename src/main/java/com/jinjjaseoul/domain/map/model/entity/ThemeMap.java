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

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ThemeMap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private Icon icon;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Category> categories = new ArrayList<>();

    @ElementCollection
    private List<String> keywordList = new ArrayList<>();

    @Embedded
    private MapSearch mapSearch; // 검색 조건 : 어디로?, 누구와?, 무엇을?, 분위기/특징, 음식, 술/음료

    @Builder
    private ThemeMap(User user, String name, Icon icon, List<Category> categories, List<String> keywordList) {
        this.user = user;
        this.name = name;
        this.icon = icon;
        this.categories = categories;
        this.keywordList = keywordList;
    }

    public boolean isMadeByUser(User user) {
        return this.user == user;
    }

    public void updateSearchCondition(Place place, Somebody somebody, Something something, Characteristics characteristics, Food food, Beverage beverage) {
        mapSearch = MapSearch.builder()
                .place(place)
                .somebody(somebody)
                .something(something)
                .characteristics(characteristics)
                .food(food)
                .beverage(beverage)
                .build();
    }

    public void subtractNumOfUserRecommend() {
        user.subtractNumOfRecommend();
    }
}