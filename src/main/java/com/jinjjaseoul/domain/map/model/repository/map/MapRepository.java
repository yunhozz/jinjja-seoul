package com.jinjjaseoul.domain.map.model.repository.map;

import com.jinjjaseoul.domain.map.model.entity.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MapRepository<T extends Map> extends JpaRepository<T, Long>, MapCustomRepository {

    boolean existsByName(String name);

    @Modifying(clearAutomatically = true)
    @Query("update Map m set m.isDeleted = true where m.id = :id")
    void deleteById(@Param("id") Long mapId);
}