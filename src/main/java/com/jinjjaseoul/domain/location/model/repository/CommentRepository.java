package com.jinjjaseoul.domain.location.model.repository;

import com.jinjjaseoul.domain.location.model.entity.Comment;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.user u where c.id = :id")
    Optional<Comment> findWithUserById(@Param("id") Long commentId);

    boolean existsByUserAndLocationAndIsDeleted(User user, Location location, boolean isDeleted);
}