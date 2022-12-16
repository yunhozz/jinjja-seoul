package com.jinjjaseoul.domain.location.model.entity;

import com.jinjjaseoul.domain.BaseEntity;
import com.jinjjaseoul.domain.icon.model.Icon;
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
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @OneToOne(fetch = FetchType.LAZY)
    private Icon icon;

    private String content;

    @Builder
    private Comment(User user, Location location, Icon icon, String content) {
        this.user = user;
        this.location = location;
        this.icon = icon;
        this.content = content;
    }
}