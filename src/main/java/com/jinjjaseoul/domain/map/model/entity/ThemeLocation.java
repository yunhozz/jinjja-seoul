package com.jinjjaseoul.domain.map.model.entity;

import com.jinjjaseoul.domain.BaseEntity;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.user.model.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
public class ThemeLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ThemeMap themeMap;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @Builder
    private ThemeLocation(User user, ThemeMap themeMap, Location location) {
        this.user = user;
        this.themeMap = themeMap;
        this.location = location;
    }

    public void updateLocation(Location location) {
        this.location = location;
    }

    public void addNumOfUserRecommend() {
        user.addNumOfRecommend();
    }

    public void subtractNumOfUserRecommend() {
        user.subtractNumOfRecommend();
    }
}