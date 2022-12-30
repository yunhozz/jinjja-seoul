package com.jinjjaseoul.domain.bookmark.model.repository;

import com.jinjjaseoul.domain.bookmark.model.entity.CurationLikes;
import com.jinjjaseoul.domain.map.model.entity.CurationMap;
import com.jinjjaseoul.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CurationLikesRepository extends JpaRepository<CurationLikes, Long> {

    boolean existsByUserAndCurationMap(User user, CurationMap curationMap);

    @Query("select cl from CurationLikes cl join fetch cl.curationMap cm where cl.id = :id")
    Optional<CurationLikes> findWithCurationMapById(@Param("id") Long curationLikesId);
}