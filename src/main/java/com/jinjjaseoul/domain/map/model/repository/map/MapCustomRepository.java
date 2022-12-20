package com.jinjjaseoul.domain.map.model.repository.map;

import com.jinjjaseoul.domain.map.dto.query.CurationMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.LocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeLocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.WholeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.request.SearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MapCustomRepository {

    // theme map
    List<ThemeMapQueryDto> findRecommendList(); // 추천 테마지도 (특징이 명확하고 개성있는 테마들을 추천)
    List<ThemeMapQueryDto> findLatestList(); // 최근 만들어진 테마지도
    List<ThemeMapQueryDto> findPopularList(); // 인기있는 테마지도
    List<ThemeLocationSimpleQueryDto> findLocationListByThemeMapId(Long themeMapId); // 특정 테마 지도의 장소 리스트 조회

    // curation map
    List<CurationMapQueryDto> findRandomList(); // 큐레이션 지도 랜덤 리스트
    List<LocationSimpleQueryDto> findLocationListByCurationMapId(Long curationMapId); // 특정 큐레이션 지도의 장소 리스트 조회

    // search
    Page<ThemeMapQueryDto> searchThemeMapListByKeyword(SearchRequestDto searchRequestDto, Long lastThemeMapId, Pageable pageable);
    Page<CurationMapQueryDto> searchCurationMapListByKeyword(SearchRequestDto searchRequestDto, Long lastCurationMapId, Pageable pageable);
    Page<WholeMapQueryDto> searchWholeMapListByKeyword(SearchRequestDto searchRequestDto, Long lastMapId, Pageable pageable);
}