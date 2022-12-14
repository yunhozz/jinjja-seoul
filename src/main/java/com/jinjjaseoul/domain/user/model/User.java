package com.jinjjaseoul.domain.user.model;

import com.jinjjaseoul.common.enums.Provider;
import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.BaseEntity;
import com.jinjjaseoul.domain.icon.model.Icon;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private String introduction;

    @OneToOne(fetch = FetchType.LAZY)
    private Icon icon;

    @Enumerated(EnumType.STRING)
    private Role role; // ROLE_ADMIN, ROLE_USER

    @Enumerated(EnumType.STRING)
    private Provider provider; // GOOGLE, KAKAO, NAVER

    private int numOfRecommend; // 테마 지도에서의 장소 추천 개수

    private int numOfComment; // 코멘트 개수

    private boolean isDeleted;

    @Builder
    private User(String email, String password, String name, String introduction, Icon icon, Role role, Provider provider) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.introduction = introduction;
        this.icon = icon;
        this.role = role;
        this.provider = provider;
    }

    public User updateInfo(String email, String name, Provider provider) {
        this.email = email;
        this.name = name;
        this.provider = provider;
        return this;
    }

    public void updateProfile(String name, String introduction, Icon icon) {
        this.name = name;
        this.introduction = introduction;
        this.icon = icon;
    }

    public void addNumOfRecommend() {
        this.numOfRecommend ++;
    }

    public void subtractNumOfRecommend() {
        if (this.numOfRecommend > 0) {
            this.numOfRecommend --;

        } else throw new IllegalStateException("기존의 추천 수가 0 이하입니다.");
    }

    public void addNumOfComment() {
        this.numOfComment++;
    }

    public void subtractNumOfComment() {
        if (this.numOfComment > 0) {
            this.numOfComment--;

        } else throw new IllegalStateException("기존의 코멘트 수가 0 이하입니다.");
    }

    public void withdraw() {
        if (!isDeleted) {
            email = "[WITHDRAW]" + email;
            isDeleted = true;

        } else throw new IllegalStateException("이미 탈퇴한 회원입니다.");
    }

    public void reAssign() {
        if (isDeleted) {
            isDeleted = false;

        } else throw new IllegalStateException("재가입이 불가능한 상태입니다.");
    }
}