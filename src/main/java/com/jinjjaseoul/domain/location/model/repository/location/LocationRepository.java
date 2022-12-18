package com.jinjjaseoul.domain.location.model.repository.location;

import com.jinjjaseoul.domain.location.model.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long>, LocationCustomRepository {

    Page<Location> findByNameContaining(String name, Pageable pageable);
}