package com.jinjjaseoul.domain.map.model.repository.curation_map;

import com.jinjjaseoul.common.enums.Beverage;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.enums.Characteristics;
import com.jinjjaseoul.common.enums.Food;
import com.jinjjaseoul.common.enums.Place;
import com.jinjjaseoul.common.enums.Somebody;
import com.jinjjaseoul.common.enums.Something;
import com.jinjjaseoul.domain.icon.model.QIcon;
import com.jinjjaseoul.domain.map.dto.query.CommentSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.CurationLocationCountQueryDto;
import com.jinjjaseoul.domain.map.dto.query.CurationMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.LocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.QCommentSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.QCurationLocationCountQueryDto;
import com.jinjjaseoul.domain.map.dto.query.QCurationMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.QLocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.request.SearchRequestDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jinjjaseoul.domain.icon.model.QIcon.icon;
import static com.jinjjaseoul.domain.location.model.entity.QComment.comment;
import static com.jinjjaseoul.domain.location.model.entity.QLocation.location;
import static com.jinjjaseoul.domain.map.model.entity.QCurationLocation.curationLocation;
import static com.jinjjaseoul.domain.map.model.entity.QCurationMap.curationMap;
import static com.jinjjaseoul.domain.user.model.QUser.user;

@Repository
@RequiredArgsConstructor
public class CurationMapCustomRepositoryImpl implements CurationMapCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CurationMapQueryDto> findRandomList() {
        QIcon curationMapIcon = new QIcon("curationMapIcon");
        QIcon userIcon = new QIcon("userIcon");

        List<CurationMapQueryDto> curationMapList = queryFactory
                .select(new QCurationMapQueryDto(
                        curationMap.id,
                        curationMap.name,
                        curationMapIcon.imageUrl,
                        user.name,
                        userIcon.imageUrl
                ))
                .from(curationMap)
                .join(curationMap.icon, curationMapIcon)
                .join(curationMap.user, user)
                .join(user.icon, userIcon)
                .orderBy(curationMap.createdDate.desc())
                .limit(50)
                .fetch();

        List<CurationLocationCountQueryDto> curationLocationList = getCurationLocationListByCurationMapIds(curationMapList);
        groupQueryAndSetLocationNum(curationMapList, curationLocationList);
        Collections.shuffle(curationMapList); // 최신 50 개의 큐레이션 지도 랜덤 정렬

        if (curationMapList.size() <= 12) {
            return curationMapList;
        }

        return new ArrayList<>(curationMapList.subList(0, 12));
    }

    @Override
    public Page<CurationMapQueryDto> searchCurationMapListByKeyword(SearchRequestDto searchRequestDto, Long lastCurationMapId, Pageable pageable) {
        QIcon curationMapIcon = new QIcon("curationMapIcon");
        QIcon userIcon = new QIcon("userIcon");

        List<CurationMapQueryDto> curationMapSearchList = queryFactory
                .select(new QCurationMapQueryDto(
                        curationMap.id,
                        curationMap.name,
                        curationMapIcon.imageUrl,
                        user.name,
                        userIcon.imageUrl
                ))
                .from(curationMap)
                .join(curationMap.icon, curationMapIcon)
                .join(curationMap.user, user)
                .join(user.icon, userIcon)
                .where(curationMapIdLt(lastCurationMapId))
                .where(
                        byKeyword(searchRequestDto.getKeyword()),
                        byPlace(searchRequestDto.getPlace()),
                        bySomebody(searchRequestDto.getSomebody()),
                        bySomething(searchRequestDto.getSomething()),
                        byCharacteristics(searchRequestDto.getCharacteristics()),
                        byFood(searchRequestDto.getFood()),
                        byBeverage(searchRequestDto.getBeverage()),
                        byCategory(searchRequestDto.getCategory())
                )
                .orderBy(curationMap.createdDate.desc())
                .limit(pageable.getPageSize())
                .fetch();

        List<CurationLocationCountQueryDto> curationLocationList = getCurationLocationListByCurationMapIds(curationMapSearchList);
        groupQueryAndSetLocationNum(curationMapSearchList, curationLocationList);

        return new PageImpl<>(curationMapSearchList, pageable, totalCount());
    }

    @Override
    public List<LocationSimpleQueryDto> findLocationListById(Long curationMapId) {
        List<LocationSimpleQueryDto> locationList = queryFactory
                .select(new QLocationSimpleQueryDto(
                        location.id,
                        location.name,
                        location.address.si,
                        location.address.gu,
                        location.address.dong,
                        location.address.etc,
                        user.id,
                        user.name,
                        user.introduction,
                        icon.imageUrl
                ))
                .from(curationLocation)
                .join(curationLocation.location, location)
                .join(curationLocation.curationMap, curationMap)
                .join(curationLocation.user, user)
                .join(user.icon, icon)
                .where(curationMap.id.eq(curationMapId))
                .orderBy(curationLocation.createdDate.desc())
                .fetch();

        List<CommentSimpleQueryDto> commentList = getCommentListByLocationIds(locationList);
        groupQueryAndSetLargestComment(locationList, commentList);

        return locationList;
    }

    private List<CurationLocationCountQueryDto> getCurationLocationListByCurationMapIds(List<CurationMapQueryDto> curationMapList) {
        List<Long> curationMapIds = curationMapList.stream()
                .map(CurationMapQueryDto::getId)
                .collect(Collectors.toList());

        return queryFactory
                .select(new QCurationLocationCountQueryDto(
                        curationLocation.id,
                        curationMap.id
                ))
                .from(curationLocation)
                .join(curationLocation.curationMap, curationMap)
                .where(curationMap.id.in(curationMapIds))
                .fetch();
    }

    private void groupQueryAndSetLocationNum(List<CurationMapQueryDto> curationMapList, List<CurationLocationCountQueryDto> curationLocationList) {
        Map<Long, List<CurationLocationCountQueryDto>> curationLocationListMap = curationLocationList.stream()
                .collect(Collectors.groupingBy(CurationLocationCountQueryDto::getCurationMapId));

        curationMapList.forEach(curationMapQueryDto -> {
            Long curationId = curationMapQueryDto.getId();
            int locationNum = curationLocationListMap.get(curationId).size();
            curationMapQueryDto.setLocationNum(locationNum);
        });
    }

    private List<CommentSimpleQueryDto> getCommentListByLocationIds(List<LocationSimpleQueryDto> locationList) {
        List<Long> locationIds = locationList.stream()
                .map(LocationSimpleQueryDto::getId)
                .collect(Collectors.toList());

        return queryFactory
                .select(new QCommentSimpleQueryDto(
                        comment.id,
                        comment.content,
                        location.id
                ))
                .from(comment)
                .join(comment.location, location)
                .where(location.id.in(locationIds))
                .orderBy(comment.content.length().desc())
                .fetch();
    }

    private void groupQueryAndSetLargestComment(List<LocationSimpleQueryDto> locationList, List<CommentSimpleQueryDto> commentList) {
        Map<Long, List<CommentSimpleQueryDto>> commentListMap = commentList.stream()
                .collect(Collectors.groupingBy(CommentSimpleQueryDto::getLocationId));

        locationList.forEach(locationSimpleQueryDto -> {
            Long locationId = locationSimpleQueryDto.getId();
            String largestComment = commentListMap.get(locationId).get(0).getContent();
            locationSimpleQueryDto.setLargestComment(largestComment);
        });
    }

    private Long totalCount() {
        return queryFactory
                .select(curationMap.count())
                .from(curationMap)
                .fetchOne();
    }

    private BooleanExpression curationMapIdLt(Long curationMapId) {
        return curationMapId != null ? curationMap.id.lt(curationMapId) : null;
    }

    private BooleanExpression byKeyword(String keyword) {
        return StringUtils.hasText(keyword) ? curationMap.name.contains(keyword) : null;
    }

    private BooleanExpression byPlace(Place place) {
        return place != null ? curationMap.mapSearch.place.eq(place) : null;
    }

    private BooleanExpression bySomebody(Somebody somebody) {
        return somebody != null ? curationMap.mapSearch.somebody.eq(somebody) : null;
    }

    private BooleanExpression bySomething(Something something) {
        return something != null ? curationMap.mapSearch.something.eq(something) : null;
    }

    private BooleanExpression byCharacteristics(Characteristics characteristics) {
        return characteristics != null ? curationMap.mapSearch.characteristics.eq(characteristics) : null;
    }

    private BooleanExpression byFood(Food food) {
        return food != null ? curationMap.mapSearch.food.eq(food) : null;
    }

    private BooleanExpression byBeverage(Beverage beverage) {
        return beverage != null ? curationMap.mapSearch.beverage.eq(beverage) : null;
    }

    private BooleanExpression byCategory(Category category) {
        return category != null ? curationMap.mapSearch.category.eq(category) : null;
    }
}