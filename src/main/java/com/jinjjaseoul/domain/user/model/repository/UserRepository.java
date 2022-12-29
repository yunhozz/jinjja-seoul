package com.jinjjaseoul.domain.user.model.repository;

import com.jinjjaseoul.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

    Optional<User> findByEmail(String email);

    @Query("select u from User u join fetch u.icon i where u.id = :id")
    Optional<User> findWithIconById(@Param("id") Long userId);

    @Query("select u.id from ThemeLocation tl join tl.user u where tl.id in :ids")
    List<Long> findIdsByThemeLocationIds(@Param("ids") List<Long> themeLocationIds);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.numOfRecommend = u.numOfRecommend - 1 where u.id in :ids")
    void subtractNumOfRecommendInIds(@Param("ids") List<Long> userIds);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.numOfRecommend = 0, u.numOfComment = 0 where u.isDeleted is false")
    void initNumOfActive();
}