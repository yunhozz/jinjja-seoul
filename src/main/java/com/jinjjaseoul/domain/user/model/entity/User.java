package com.jinjjaseoul.domain.user.model.entity;

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
    private Provider provider; // GOOGLE, KAKAO, APPLE

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
}