package com.jinjjaseoul.domain.location.model.repository.location;

import com.jinjjaseoul.domain.location.dto.query.CommentQueryDto;
import com.jinjjaseoul.domain.location.dto.query.LocationQueryDto;
import com.jinjjaseoul.domain.location.dto.query.QCommentQueryDto;
import com.jinjjaseoul.domain.location.dto.query.QLocationQueryDto;
import com.jinjjaseoul.domain.location.dto.query.QThemeMapNameQueryDto;
import com.jinjjaseoul.domain.location.dto.query.ThemeMapNameQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jinjjaseoul.domain.icon.model.QIcon.icon;
import static com.jinjjaseoul.domain.location.model.entity.QComment.comment;
import static com.jinjjaseoul.domain.location.model.entity.QLocation.location;
import static com.jinjjaseoul.domain.map.model.entity.QCurationLocation.curationLocation;
import static com.jinjjaseoul.domain.map.model.entity.QThemeLocation.themeLocation;
import static com.jinjjaseoul.domain.map.model.entity.QThemeMap.themeMap;

@Repository
@RequiredArgsConstructor
public class LocationCustomRepositoryImpl implements LocationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public LocationQueryDto findLocationInfoById(Long locationId) {
        LocationQueryDto locationQueryDto = queryFactory
                .select(new QLocationQueryDto(
                        location.id,
                        location.name,
                        location.address.si,
                        location.address.gu,
                        location.address.dong,
                        location.address.etc,
                        location.nx,
                        location.ny
                ))
                .from(location)
                .where(location.id.eq(locationId))
                .fetchOne();

        List<String> imageUrlList = getImageUrlList(locationQueryDto.getId());
        List<ThemeMapNameQueryDto> themeMapNameList = getThemeMapNameQueryDtoList(locationQueryDto.getId());
        List<CommentQueryDto> comments = getCommentQueryDtoList(locationQueryDto.getId());

        locationQueryDto.setImageUrlList(imageUrlList);
        locationQueryDto.setThemeMapNameList(themeMapNameList);
        locationQueryDto.setComments(comments);

        return locationQueryDto;
    }

    private List<String> getImageUrlList(Long locationId) {
        List<String> themeLocationImgList = queryFactory
                .select(themeLocation.imageUrl)
                .from(themeLocation)
                .join(themeLocation.location, location)
                .where(location.id.eq(locationId))
                .orderBy(themeLocation.createdDate.desc())
                .limit(5)
                .fetch();

        List<String> curationLocationImgList = queryFactory
                .select(curationLocation.imageUrl)
                .from(curationLocation)
                .join(curationLocation.location, location)
                .where(location.id.eq(locationId))
                .orderBy(curationLocation.createdDate.desc())
                .limit(5)
                .fetch();

        List<String> imageUrlList = new ArrayList<>();
        Collections.addAll(imageUrlList, themeLocationImgList.toArray(new String[0]));
        Collections.addAll(imageUrlList, curationLocationImgList.toArray(new String[0]));

        return imageUrlList;
    }

    private List<ThemeMapNameQueryDto> getThemeMapNameQueryDtoList(Long locationId) {
        return queryFactory
                .select(new QThemeMapNameQueryDto(
                        themeMap.name,
                        icon.imageUrl
                ))
                .from(themeLocation)
                .join(themeLocation.themeMap, themeMap)
                .join(themeLocation.location, location)
                .join(themeMap.icon, icon)
                .where(location.id.eq(locationId))
                .orderBy(themeMap.createdDate.desc())
                .fetch();
    }

    private List<CommentQueryDto> getCommentQueryDtoList(Long locationId) {
        return queryFactory
                .select(new QCommentQueryDto(
                        icon.imageUrl,
                        comment.content
                ))
                .from(comment)
                .join(comment.icon, icon)
                .join(comment.location, location)
                .where(location.id.eq(locationId))
                .orderBy(comment.createdDate.desc())
                .fetch();
    }
}