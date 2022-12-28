package com.jinjjaseoul.domain.location.model.repository;

import com.jinjjaseoul.domain.location.model.entity.Image;
import com.jinjjaseoul.domain.location.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByLocation(Location location);

    @Query("select i.savedPath from Image i where i.id = :id")
    Optional<String> findPathById(@Param("id") Long imageId);
}