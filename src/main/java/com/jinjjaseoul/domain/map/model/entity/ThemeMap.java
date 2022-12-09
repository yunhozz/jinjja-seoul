package com.jinjjaseoul.domain.map.model.entity;

import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.domain.BaseEntity;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.user.model.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
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

    private String keyword;

    @Builder
    private ThemeMap(User user, String name, Icon icon, List<Category> categories, String keyword) {
        this.user = user;
        this.name = name;
        this.icon = icon;
        this.categories = categories;
        this.keyword = keyword;
    }
}