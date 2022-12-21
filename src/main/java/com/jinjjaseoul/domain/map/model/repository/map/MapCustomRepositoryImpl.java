package com.jinjjaseoul.domain.map.model.repository.map;

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
import com.jinjjaseoul.domain.map.dto.query.QThemeLocationCountQueryDto;
import com.jinjjaseoul.domain.map.dto.query.QThemeLocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.QThemeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.QWholeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeLocationCountQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeLocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.WholeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.request.SearchRequestDto;
import com.querydsl.core.BooleanBuilder;
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
import static com.jinjjaseoul.domain.map.model.entity.QMap.map;
import static com.jinjjaseoul.domain.map.model.entity.QThemeLocation.themeLocation;
import static com.jinjjaseoul.domain.map.model.entity.QThemeMap.themeMap;
import static com.jinjjaseoul.domain.user.model.QUser.user;

@Repository
@RequiredArgsConstructor
public class MapCustomRepositoryImpl implements MapCustomRepository {

    private final JPAQueryFactory queryFactory;

    // TODO: 2022-12-12 추천 리스트
    @Override
    public List<ThemeMapQueryDto> findRecommendList() {
        return null;
    }

    @Override
    public List<ThemeMapQueryDto> findLatestList() {
        List<ThemeMapQueryDto> themeMapList = queryFactory
                .select(new QThemeMapQueryDto(
                        themeMap.id,
                        themeMap.name,
                        icon.imageUrl
                ))
                .from(themeMap)
                .join(themeMap.icon, icon)
                .orderBy(themeMap.createdDate.desc())
                .limit(9)
                .fetch();

        List<Long> themeMapIds = getThemeMapIds(themeMapList);
        List<ThemeLocationCountQueryDto> themeLocationList = getThemeLocationDtoListByThemeMapIds(themeMapIds);
        groupQueryAndSetCuratorNum(themeMapList, themeLocationList);

        return themeMapList;
    }

    @Override
    public List<ThemeMapQueryDto> findPopularList() {
        List<ThemeMapQueryDto> themeMapList = queryFactory
                .select(new QThemeMapQueryDto(
                        themeMap.id,
                        themeMap.name,
                        icon.imageUrl
                ))
                .from(themeMap)
                .join(themeMap.icon, icon)
                .fetch();

        List<Long> themeMapIds = getThemeMapIds(themeMapList);
        List<ThemeLocationCountQueryDto> themeLocationList = getThemeLocationDtoListByThemeMapIds(themeMapIds);
        groupQueryAndSetCuratorNum(themeMapList, themeLocationList);
        // 큐레이터 수 내림차순 정렬, 같을 시 id 값 내림차순 정렬
        themeMapList.sort((o1, o2) -> o1.getCuratorNum() != o2.getCuratorNum() ? o2.getCuratorNum() - o1.getCuratorNum() : Math.toIntExact(o2.getId() - o1.getId()));

        return themeMapList.size() > 12 ? new ArrayList<>(themeMapList.subList(0, 12)) : themeMapList;
    }

    @Override
    public List<ThemeLocationSimpleQueryDto> findLocationListByThemeMapId(Long themeMapId) {
        return queryFactory
                .select(new QThemeLocationSimpleQueryDto(
                        themeLocation.id,
                        location.id,
                        location.name,
                        icon.imageUrl
                ))
                .from(themeLocation)
                .join(themeLocation.location, location)
                .join(themeLocation.user, user)
                .join(user.icon, icon)
                .join(themeLocation.themeMap, themeMap)
                .where(themeMap.id.eq(themeMapId))
                .fetch();
    }

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

        List<Long> curationMapIds = getCurationMapIds(curationMapList);
        List<CurationLocationCountQueryDto> curationLocationList = getCurationLocationDtoListByCurationMapIds(curationMapIds);
        groupQueryAndSetLocationNum(curationMapList, curationLocationList);
        Collections.shuffle(curationMapList); // 최신 50 개의 큐레이션 지도 랜덤 정렬

        return curationMapList.size() > 12 ? new ArrayList<>(curationMapList.subList(0, 12)) : curationMapList;
    }

    @Override
    public List<LocationSimpleQueryDto> findLocationListByCurationMapId(Long curationMapId) {
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

        List<CommentSimpleQueryDto> commentList = getCommentListByLocationIdsOrderByCommentLength(locationList);
        groupQueryAndSetLargestComment(locationList, commentList);

        return locationList;
    }

    @Override
    public Page<ThemeMapQueryDto> searchThemeMapListByKeyword(SearchRequestDto searchRequestDto, Long lastThemeMapId, Pageable pageable) {
        List<ThemeMapQueryDto> themeMapSearchList = queryFactory
                .select(new QThemeMapQueryDto(
                        themeMap.id,
                        themeMap.name,
                        icon.imageUrl
                ))
                .from(themeMap)
                .join(themeMap.icon, icon)
                .join(map).on(themeMap.eq(map))
                .where(mapIdLt(lastThemeMapId))
                .where(
                        byThemeMapKeyword(searchRequestDto.getKeyword()),
                        byPlace(searchRequestDto.getPlace()),
                        bySomebody(searchRequestDto.getSomebody()),
                        bySomething(searchRequestDto.getSomething()),
                        byCharacteristics(searchRequestDto.getCharacteristics()),
                        byFood(searchRequestDto.getFood()),
                        byBeverage(searchRequestDto.getBeverage()),
                        byThemeMapCategories(searchRequestDto.getCategories())
                )
                .orderBy(themeMap.createdDate.desc())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> themeMapIds = getThemeMapIds(themeMapSearchList);
        List<ThemeLocationCountQueryDto> themeLocationList = getThemeLocationDtoListByThemeMapIds(themeMapIds);
        groupQueryAndSetCuratorNum(themeMapSearchList, themeLocationList);

        return new PageImpl<>(themeMapSearchList, pageable, themeMapTotalCount());
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
                .join(map).on(curationMap.eq(map))
                .where(mapIdLt(lastCurationMapId))
                .where(
                        byCurationMapKeyword(searchRequestDto.getKeyword()),
                        byPlace(searchRequestDto.getPlace()),
                        bySomebody(searchRequestDto.getSomebody()),
                        bySomething(searchRequestDto.getSomething()),
                        byCharacteristics(searchRequestDto.getCharacteristics()),
                        byFood(searchRequestDto.getFood()),
                        byBeverage(searchRequestDto.getBeverage()),
                        byCurationMapCategories(searchRequestDto.getCategories())
                )
                .orderBy(curationMap.createdDate.desc())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> curationMapIds = getCurationMapIds(curationMapSearchList);
        List<CurationLocationCountQueryDto> curationLocationList = getCurationLocationDtoListByCurationMapIds(curationMapIds);
        groupQueryAndSetLocationNum(curationMapSearchList, curationLocationList);

        return new PageImpl<>(curationMapSearchList, pageable, curationMapTotalCount());
    }

    @Override
    public Page<WholeMapQueryDto> searchWholeMapListByKeyword(SearchRequestDto searchRequestDto, Long lastMapId, Pageable pageable) {
        QIcon mapIcon = new QIcon("mapIcon");
        QIcon userIcon = new QIcon("userIcon");

        List<WholeMapQueryDto> mapList = queryFactory
                .select(new QWholeMapQueryDto(
                        map.id,
                        map.name,
                        mapIcon.imageUrl,
                        map.dtype,
                        user.name,
                        userIcon.imageUrl
                ))
                .from(map)
                .join(map.icon, mapIcon)
                .join(map.user, user)
                .join(user.icon, userIcon)
                .join(themeMap).on(themeMap.eq(map))
                .join(curationMap).on(curationMap.eq(map))
                .where(mapIdLt(lastMapId))
                .where(
                        byKeywordAccordingToType(searchRequestDto.getKeyword()),
                        byCategoriesAccordingToType(searchRequestDto.getCategories()),
                        byPlace(searchRequestDto.getPlace()),
                        bySomebody(searchRequestDto.getSomebody()),
                        bySomething(searchRequestDto.getSomething()),
                        byCharacteristics(searchRequestDto.getCharacteristics()),
                        byFood(searchRequestDto.getFood()),
                        byBeverage(searchRequestDto.getBeverage())
                )
                .orderBy(map.createdDate.desc())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> themeMapIds = new ArrayList<>();
        List<Long> curationMapIds = new ArrayList<>();

        for (WholeMapQueryDto wholeMapQueryDto : mapList) {
            Long mapId = wholeMapQueryDto.getId();
            if (wholeMapQueryDto.getDtype().equals("TM")) {
                themeMapIds.add(mapId);

            } else curationMapIds.add(mapId);
        }

        List<ThemeLocationCountQueryDto> themeLocationList = getThemeLocationDtoListByThemeMapIds(themeMapIds);
        List<CurationLocationCountQueryDto> curationLocationList = getCurationLocationDtoListByCurationMapIds(curationMapIds);
        groupQueryAndSetExtraColumns(mapList, themeLocationList, curationLocationList);

        return new PageImpl<>(mapList, pageable, wholeMapTotalCount());
    }

    private List<Long> getThemeMapIds(List<ThemeMapQueryDto> themeMapQueryDtoList) {
        return themeMapQueryDtoList.stream()
                .map(ThemeMapQueryDto::getId)
                .collect(Collectors.toList());
    }

    private List<Long> getCurationMapIds(List<CurationMapQueryDto> curationMapList) {
        return curationMapList.stream()
                .map(CurationMapQueryDto::getId)
                .collect(Collectors.toList());
    }

    private List<ThemeLocationCountQueryDto> getThemeLocationDtoListByThemeMapIds(List<Long> themeMapIds) {
        return queryFactory
                .select(new QThemeLocationCountQueryDto(
                        themeLocation.id,
                        themeMap.id
                ))
                .from(themeLocation)
                .join(themeLocation.themeMap, themeMap)
                .where(themeMap.id.in(themeMapIds))
                .fetch();
    }

    private List<CurationLocationCountQueryDto> getCurationLocationDtoListByCurationMapIds(List<Long> curationMapIds) {
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

    private List<CommentSimpleQueryDto> getCommentListByLocationIdsOrderByCommentLength(List<LocationSimpleQueryDto> locationList) {
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

    private void groupQueryAndSetCuratorNum(List<ThemeMapQueryDto> themeMapList, List<ThemeLocationCountQueryDto> themeLocationList) {
        Map<Long, List<ThemeLocationCountQueryDto>> themeLocationListMap = groupThemeLocationById(themeLocationList);
        themeMapList.forEach(themeMapQueryDto -> {
            Long themeMapId = themeMapQueryDto.getId();
            List<ThemeLocationCountQueryDto> themeLocationDtoList = themeLocationListMap.get(themeMapId);

            if (themeLocationDtoList != null) {
                int curatorNum = themeLocationDtoList.size();
                themeMapQueryDto.setCuratorNum(curatorNum);
            }
        });
    }

    private void groupQueryAndSetLocationNum(List<CurationMapQueryDto> curationMapList, List<CurationLocationCountQueryDto> curationLocationList) {
        Map<Long, List<CurationLocationCountQueryDto>> curationLocationListMap = groupCurationLocationById(curationLocationList);
        curationMapList.forEach(curationMapQueryDto -> {
            Long curationId = curationMapQueryDto.getId();
            List<CurationLocationCountQueryDto> curationLocationDtoList = curationLocationListMap.get(curationId);

            if (curationLocationDtoList != null) {
                int locationNum = curationLocationDtoList.size();
                curationMapQueryDto.setLocationNum(locationNum);
            }
        });
    }

    private void groupQueryAndSetLargestComment(List<LocationSimpleQueryDto> locationList, List<CommentSimpleQueryDto> commentList) {
        Map<Long, List<CommentSimpleQueryDto>> commentListMap = commentList.stream()
                .collect(Collectors.groupingBy(CommentSimpleQueryDto::getLocationId));

        locationList.forEach(locationSimpleQueryDto -> {
            Long locationId = locationSimpleQueryDto.getId();
            List<CommentSimpleQueryDto> comments = commentListMap.get(locationId);

            if (comments != null) {
                String largestComment = comments.get(0).getContent();
                locationSimpleQueryDto.setLargestComment(largestComment);
            }
        });
    }

    private void groupQueryAndSetExtraColumns(List<WholeMapQueryDto> mapList, List<ThemeLocationCountQueryDto> themeLocationList,
                                              List<CurationLocationCountQueryDto> curationLocationList) {
        Map<Long, List<ThemeLocationCountQueryDto>> themeLocationListMap = groupThemeLocationById(themeLocationList);
        Map<Long, List<CurationLocationCountQueryDto>> curationLocationListMap = groupCurationLocationById(curationLocationList);

        mapList.forEach(wholeMapQueryDto -> {
            Long mapId = wholeMapQueryDto.getId();
            List<ThemeLocationCountQueryDto> themeLocationDtoList = themeLocationListMap.get(mapId);
            List<CurationLocationCountQueryDto> curationLocationDtoList = curationLocationListMap.get(mapId);

            if (themeLocationDtoList != null) {
                int curatorNum = themeLocationDtoList.size();
                wholeMapQueryDto.setCuratorNum(curatorNum);
            }

            if (curationLocationDtoList != null) {
                int locationNum = curationLocationDtoList.size();
                wholeMapQueryDto.setLocationNum(locationNum);
            }
        });
    }

    private Map<Long, List<ThemeLocationCountQueryDto>> groupThemeLocationById(List<ThemeLocationCountQueryDto> themeLocationList) {
        return themeLocationList.stream()
                .collect(Collectors.groupingBy(ThemeLocationCountQueryDto::getThemeMapId));
    }

    private Map<Long, List<CurationLocationCountQueryDto>> groupCurationLocationById(List<CurationLocationCountQueryDto> curationLocationList) {
        return curationLocationList.stream()
                .collect(Collectors.groupingBy(CurationLocationCountQueryDto::getCurationMapId));
    }

    private Long themeMapTotalCount() {
        return queryFactory
                .select(themeMap.count())
                .from(themeMap)
                .fetchOne();
    }

    private Long curationMapTotalCount() {
        return queryFactory
                .select(curationMap.count())
                .from(curationMap)
                .fetchOne();
    }

    private Long wholeMapTotalCount() {
        return queryFactory
                .select(map.count())
                .from(map)
                .fetchOne();
    }

    private BooleanExpression mapIdLt(Long lastMapId) {
        return lastMapId != null ? map.id.lt(lastMapId) : null;
    }

    private BooleanExpression byPlace(Place place) {
        return place != null ? map.mapSearch.place.eq(place) : null;
    }

    private BooleanExpression bySomebody(Somebody somebody) {
        return somebody != null ? map.mapSearch.somebody.eq(somebody) : null;
    }

    private BooleanExpression bySomething(Something something) {
        return something != null ? map.mapSearch.something.eq(something) : null;
    }

    private BooleanExpression byCharacteristics(Characteristics characteristics) {
        return characteristics != null ? map.mapSearch.characteristics.eq(characteristics) : null;
    }

    private BooleanExpression byFood(Food food) {
        return food != null ? map.mapSearch.food.eq(food) : null;
    }

    private BooleanExpression byBeverage(Beverage beverage) {
        return beverage != null ? map.mapSearch.beverage.eq(beverage) : null;
    }

    private BooleanExpression byThemeMapKeyword(String keyword) {
        return StringUtils.hasText(keyword) ? themeMap.keywordList.any().like(keyword) : null;
    }

    private BooleanExpression byCurationMapKeyword(String keyword) {
        return StringUtils.hasText(keyword) ? curationMap.dtype.eq("CM").and(curationMap.name.contains(keyword)) : null;
    }

    private BooleanExpression byThemeMapCategories(List<Category> categories) {
        return categories != null ? themeMap.categories.any().in(categories) : null;
    }

    private BooleanExpression byCurationMapCategories(List<Category> categories) {
        return categories != null ? curationMap.dtype.eq("CM").and(curationMap.mapSearch.category.in(categories)) : null;
    }

    private BooleanBuilder byKeywordAccordingToType(String keyword) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(byThemeMapKeyword(keyword));
        builder.or(byCurationMapKeyword(keyword));

        return builder;
    }

    private BooleanBuilder byCategoriesAccordingToType(List<Category> categories) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(byThemeMapCategories(categories));
        builder.or(byCurationMapCategories(categories));

        return builder;
    }
}