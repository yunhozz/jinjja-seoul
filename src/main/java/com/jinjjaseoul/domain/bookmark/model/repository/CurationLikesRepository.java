package com.jinjjaseoul.domain.bookmark.model.repository;

import com.jinjjaseoul.domain.bookmark.model.entity.CurationLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurationLikesRepository extends JpaRepository<CurationLikes, Long> {
}