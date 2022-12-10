package com.jinjjaseoul.domain.bookmark.model.repository;

import com.jinjjaseoul.domain.bookmark.model.entity.LocationBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationBookmarkRepository extends JpaRepository<LocationBookmark, Long> {
}