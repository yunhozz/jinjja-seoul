package com.jinjjaseoul.domain.map.model.repository.map;

import com.jinjjaseoul.domain.map.model.entity.Map;
import com.jinjjaseoul.domain.map.model.entity.ThemeMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MapRepository<T extends Map> extends JpaRepository<T, Long>, MapCustomRepository {

    @Query("select tm from ThemeMap tm join fetch tm.user u where tm.id = :id")
    Optional<ThemeMap> findWithUserById(@Param("id") Long themeMapId);

    boolean existsByName(String name);
}