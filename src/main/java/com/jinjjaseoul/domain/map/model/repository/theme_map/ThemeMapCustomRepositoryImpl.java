package com.jinjjaseoul.domain.map.model.repository.theme_map;

import com.jinjjaseoul.common.enums.Beverage;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.enums.Characteristics;
import com.jinjjaseoul.common.enums.Food;
import com.jinjjaseoul.common.enums.Place;
import com.jinjjaseoul.common.enums.Somebody;
import com.jinjjaseoul.common.enums.Something;
import com.jinjjaseoul.domain.map.dto.query.QThemeLocationCountQueryDto;
import com.jinjjaseoul.domain.map.dto.query.QThemeLocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.QThemeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.SearchQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeLocationCountQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeLocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeMapQueryDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jinjjaseoul.domain.icon.model.QIcon.icon;
import static com.jinjjaseoul.domain.location.model.entity.QLocation.location;
import static com.jinjjaseoul.domain.map.model.entity.QThemeLocation.themeLocation;
import static com.jinjjaseoul.domain.map.model.entity.QThemeMap.themeMap;
import static com.jinjjaseoul.domain.user.model.QUser.user;

@Repository
@RequiredArgsConstructor
public class ThemeMapCustomRepositoryImpl implements ThemeMapCustomRepository {

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
                        icon.id,
                        icon.imageUrl
                ))
                .from(themeMap)
                .join(themeMap.icon, icon)
                .orderBy(themeMap.createdDate.desc())
                .limit(9)
                .fetch();

        List<ThemeLocationCountQueryDto> themeLocationList = getThemeLocationListByThemeMapIds(themeMapList);
        groupQueryAndSetCuratorNum(themeMapList, themeLocationList);

        return themeMapList;
    }

    @Override
    public List<ThemeMapQueryDto> findPopularList() {
        List<ThemeMapQueryDto> themeMapList = queryFactory
                .select(new QThemeMapQueryDto(
                        themeMap.id,
                        themeMap.name,
                        icon.id,
                        icon.imageUrl
                ))
                .from(themeMap)
                .join(themeMap.icon, icon)
                .fetch();

        List<ThemeLocationCountQueryDto> themeLocationList = getThemeLocationListByThemeMapIds(themeMapList);
        groupQueryAndSetCuratorNum(themeMapList, themeLocationList);
        themeMapList.sort((o1, o2) -> o2.getCuratorNum() - o1.getCuratorNum()); // 큐레이터 수 내림차순 정렬

        return new ArrayList<>(themeMapList.subList(0, 12));
    }

    @Override
    public Page<ThemeMapQueryDto> searchThemeMapListByKeyword(String keyword, SearchQueryDto searchQueryDto, Long lastThemeMapId, Pageable pageable) {
        List<ThemeMapQueryDto> themeMapSearchList = queryFactory
                .select(new QThemeMapQueryDto(
                        themeMap.id,
                        themeMap.name,
                        icon.id,
                        icon.imageUrl
                ))
                .from(themeMap)
                .join(themeMap.icon, icon)
                .where(themeMapIdLt(lastThemeMapId))
                .where(
                        byKeyword(keyword),
                        byPlace(searchQueryDto.getPlace()),
                        bySomebody(searchQueryDto.getSomebody()),
                        bySomething(searchQueryDto.getSomething()),
                        byCharacteristics(searchQueryDto.getCharacteristics()),
                        byFood(searchQueryDto.getFood()),
                        byBeverage(searchQueryDto.getBeverage()),
                        byCategory(searchQueryDto.getCategory())
                )
                .orderBy(themeMap.createdDate.desc())
                .limit(pageable.getPageSize())
                .fetch();

        List<ThemeLocationCountQueryDto> themeLocationList = getThemeLocationListByThemeMapIds(themeMapSearchList);
        groupQueryAndSetCuratorNum(themeMapSearchList, themeLocationList);

        return new PageImpl<>(themeMapSearchList, pageable, totalCount());
    }

    @Override
    public List<ThemeLocationSimpleQueryDto> findLocationListById(Long themeMapId) {
        return queryFactory
                .select(new QThemeLocationSimpleQueryDto(
                        themeLocation.id,
                        location.id,
                        location.name,
                        icon.id,
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

    private List<ThemeLocationCountQueryDto> getThemeLocationListByThemeMapIds(List<ThemeMapQueryDto> themeMapQueryDtoList) {
        List<Long> themeMapIds = themeMapQueryDtoList.stream()
                .map(ThemeMapQueryDto::getId)
                .collect(Collectors.toList());

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

    private void groupQueryAndSetCuratorNum(List<ThemeMapQueryDto> themeMapList, List<ThemeLocationCountQueryDto> themeLocationList) {
        Map<Long, List<ThemeLocationCountQueryDto>> themeLocationListMap = themeLocationList.stream()
                .collect(Collectors.groupingBy(ThemeLocationCountQueryDto::getThemeMapId));
        themeMapList.forEach(themeMapQueryDto -> themeMapQueryDto.setCuratorNum(themeLocationListMap.get(themeMapQueryDto.getId()).size()));
    }

    private Long totalCount() {
        return queryFactory
                .select(themeMap.count())
                .from(themeMap)
                .fetchOne();
    }

    private BooleanExpression themeMapIdLt(Long lastThemeMapId) {
        return lastThemeMapId != null ? themeMap.id.lt(lastThemeMapId) : null;
    }

    private BooleanExpression byKeyword(String keyword) {
        return StringUtils.hasText(keyword) ? themeMap.keywordList.any().like(keyword) : null;
    }

    private BooleanExpression byPlace(Place place) {
        return place != null ? themeMap.mapSearch.place.eq(place) : null;
    }

    private BooleanExpression bySomebody(Somebody somebody) {
        return somebody != null ? themeMap.mapSearch.somebody.eq(somebody) : null;
    }

    private BooleanExpression bySomething(Something something) {
        return something != null ? themeMap.mapSearch.something.eq(something) : null;
    }

    private BooleanExpression byCharacteristics(Characteristics characteristics) {
        return characteristics != null ? themeMap.mapSearch.characteristics.eq(characteristics) : null;
    }

    private BooleanExpression byFood(Food food) {
        return food != null ? themeMap.mapSearch.food.eq(food) : null;
    }

    private BooleanExpression byBeverage(Beverage beverage) {
        return beverage != null ? themeMap.mapSearch.beverage.eq(beverage) : null;
    }

    private BooleanExpression byCategory(Category category) {
        return category != null ? themeMap.categories.any().eq(category) : null;
    }
}