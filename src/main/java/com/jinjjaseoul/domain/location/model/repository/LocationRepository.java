package com.jinjjaseoul.domain.location.model.repository;

import com.jinjjaseoul.domain.location.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}