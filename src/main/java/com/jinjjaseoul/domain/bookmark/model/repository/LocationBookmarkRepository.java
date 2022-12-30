package com.jinjjaseoul.domain.bookmark.model.repository;

import com.jinjjaseoul.domain.bookmark.model.entity.LocationBookmark;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationBookmarkRepository extends JpaRepository<LocationBookmark, Long> {

    @Query("select l.id from LocationBookmark lb join lb.user u join lb.location l where u.id = :id")
    List<Long> findLocationIdsByUserId(@Param("id") Long userId);

    boolean existsByUserAndLocation(User user, Location location);
}