package com.jinjjaseoul.domain.location.model.entity;

import com.jinjjaseoul.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    private Address address;

    private String nx;

    private String ny;

    @Builder
    private Location(String name, String si, String gu, String dong, String etc, String nx, String ny) {
        this.name = name;
        this.address = new Address(si, gu, dong, etc);
        this.nx = nx;
        this.ny = ny;
    }
}