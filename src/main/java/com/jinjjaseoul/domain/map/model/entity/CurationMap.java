package com.jinjjaseoul.domain.map.model.entity;

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

    public void addLikes() {
        numOfLikes++;
    }

    public void subtractLikes() {
        if (numOfLikes > 0) {
            numOfLikes--;

        } else throw new IllegalStateException("좋아요 수를 더 이상 뺄 수 없습니다.");
    }
}