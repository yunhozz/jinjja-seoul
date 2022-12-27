package com.jinjjaseoul.domain.user.model.repository;

import com.jinjjaseoul.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

    Optional<User> findByEmail(String email);

    @Query("select u from User u join fetch u.icon i where u.id = :id")
    Optional<User> findWithIconById(@Param("id") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.numOfRecommend = 0, u.numOfComment = 0")
    void initNumOfActive();
}