package com.jinjjaseoul.domain.location.model.repository;

import com.jinjjaseoul.domain.location.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}