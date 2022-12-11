package com.jinjjaseoul.domain.map.model.repository;

import com.jinjjaseoul.domain.map.model.entity.ThemeLocation;
import com.jinjjaseoul.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeLocationRepository extends JpaRepository<ThemeLocation, Long> {

    Optional<ThemeLocation> findByUser(User user);
}