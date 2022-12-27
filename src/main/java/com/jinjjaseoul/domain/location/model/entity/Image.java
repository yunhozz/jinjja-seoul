package com.jinjjaseoul.domain.location.model.entity;

import com.jinjjaseoul.domain.BaseEntity;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    private String originalName;

    private String savedName;

    private String savedPath;

    @Builder
    private Image(Location location, String originalName, String savedName, String savedPath) {
        this.location = location;
        this.originalName = originalName;
        this.savedName = savedName;
        this.savedPath = savedPath;
    }
}