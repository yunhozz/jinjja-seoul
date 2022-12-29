package com.jinjjaseoul.domain.map.model.entity;

import com.jinjjaseoul.domain.BaseEntity;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.user.model.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Getter
//@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Map extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    private Icon icon;

    @Embedded
    private MapSearch mapSearch; // 검색 조건 : 어디로?, 누구와?, 무엇을?, 분위기/특징, 음식, 술/음료, 카테고리 (큐레이션 지도)

    private boolean isDeleted;

    @Column(insertable = false, updatable = false)
    private String dtype; // querydsl 을 위해 @DiscriminatorColumn 대신 직접 명시

    public Map(User user, String name, Icon icon) {
        this.user = user;
        this.name = name;
        this.icon = icon;
    }

    public void updateMapSearch(MapSearch mapSearch) {
        this.mapSearch = mapSearch;
    }
}