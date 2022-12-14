package com.jinjjaseoul.domain.map.model.repository;

import com.jinjjaseoul.domain.map.model.entity.ThemeLocation;
import com.jinjjaseoul.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThemeLocationRepository extends JpaRepository<ThemeLocation, Long> {

    Optional<ThemeLocation> findByUser(User user);

    @Query("select tl from ThemeLocation tl join fetch tl.user u where tl.id = :id")
    Optional<ThemeLocation> findWithUserById(@Param("id") Long themeLocationId);

    @Query("select tl.id from ThemeLocation tl join tl.themeMap tm where tm.id = :id")
    List<Long> findIdsByThemeMapId(@Param("id") Long themeMapId);

    @Modifying(clearAutomatically = true)
    @Query("delete from ThemeLocation tl where tl.id in :ids")
    void deleteAllByIds(@Param("ids") List<Long> themeLocationIds);
}