package com.jinjjaseoul.domain.map.model.repository.map;

import com.jinjjaseoul.domain.map.model.entity.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapRepository<T extends Map> extends JpaRepository<T, Long>, MapCustomRepository {

    boolean existsByName(String name);
}