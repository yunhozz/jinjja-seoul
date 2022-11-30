package com.jinjjaseoul.domain.user.model;

import com.jinjjaseoul.common.enums.Role;
import com.jinjjaseoul.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role; // ROLE_ADMIN, ROLE_USER

    @Builder
    private User(String email, String password, String name, String imageUrl, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.imageUrl = imageUrl;
        this.role = role;
    }
}