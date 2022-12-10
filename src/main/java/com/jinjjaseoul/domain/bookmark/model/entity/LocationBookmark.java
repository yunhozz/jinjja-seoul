package com.jinjjaseoul.domain.bookmark.model.entity;

import com.jinjjaseoul.domain.BaseEntity;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.user.model.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    public LocationBookmark(User user, Location location) {
        this.user = user;
        this.location = location;
    }
}