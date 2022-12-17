package com.jinjjaseoul.domain.user.model.repository;

import com.jinjjaseoul.domain.user.dto.query.ProfileQueryDto;
import com.jinjjaseoul.domain.user.dto.query.QProfileQueryDto;
import com.jinjjaseoul.domain.user.dto.query.QUserCardQueryDto;
import com.jinjjaseoul.domain.user.dto.query.UserCardQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jinjjaseoul.domain.icon.model.QIcon.icon;
import static com.jinjjaseoul.domain.user.model.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public ProfileQueryDto findProfileById(Long userId) {
        return queryFactory
                .select(new QProfileQueryDto(
                        user.id,
                        user.name,
                        user.introduction,
                        icon.imageUrl
                ))
                .from(user)
                .join(user.icon, icon)
                .where(user.id.eq(userId))
                .fetchOne();
    }

    @Override
    public List<UserCardQueryDto> findDiligentCurators() {
        List<UserCardQueryDto> users = queryFactory
                .select(new QUserCardQueryDto(
                        user.id,
                        user.name,
                        user.numOfRecommend,
                        icon.imageUrl
                ))
                .from(user)
                .join(user.icon, icon)
                .orderBy(user.numOfRecommend.add(user.numOfComment).desc())
                .limit(20)
                .fetch();

        Collections.shuffle(users); // 상위 20 명의 유저를 랜덤 배치

        if (users.size() <= 6) {
            return users;
        }

        return new ArrayList<>(users.subList(0, 6));
    }
}