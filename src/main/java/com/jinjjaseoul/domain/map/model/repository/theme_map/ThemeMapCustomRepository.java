package com.jinjjaseoul.domain.map.model.repository.theme_map;

import com.jinjjaseoul.domain.map.dto.request.SearchRequestDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeLocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeMapQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ThemeMapCustomRepository {

    List<ThemeMapQueryDto> findRecommendList(); // 추천 테마지도 (특징이 명확하고 개성있는 테마들을 추천)
    List<ThemeMapQueryDto> findLatestList(); // 최근 만들어진 테마지도
    List<ThemeMapQueryDto> findPopularList(); // 인기있는 테마지도
    Page<ThemeMapQueryDto> searchThemeMapListByKeyword(String keyword, SearchRequestDto searchRequestDto, Long lastThemeMapId, Pageable pageable);// 지도 찾기, 커서 페이징 방식
    List<ThemeLocationSimpleQueryDto> findLocationListById(Long themeMapId); // 특정 테마 지도의 장소 리스트 조회
}