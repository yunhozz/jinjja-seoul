package com.jinjjaseoul.domain.location.model.repository;

import com.jinjjaseoul.domain.location.model.entity.Image;
import com.jinjjaseoul.domain.location.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByLocation(Location location);
}