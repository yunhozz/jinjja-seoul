package com.jinjjaseoul.domain.map.model.repository.theme_map;

import com.jinjjaseoul.domain.map.model.entity.ThemeMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeMapRepository extends JpaRepository<ThemeMap, Long>, ThemeMapCustomRepository {
}