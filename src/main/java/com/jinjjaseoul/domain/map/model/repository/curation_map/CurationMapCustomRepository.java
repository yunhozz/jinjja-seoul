package com.jinjjaseoul.domain.map.model.repository.curation_map;

import com.jinjjaseoul.domain.map.dto.query.LocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.CurationMapQueryDto;
import com.jinjjaseoul.domain.map.dto.request.SearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CurationMapCustomRepository {

    List<CurationMapQueryDto> findRandomList();
    Page<CurationMapQueryDto> searchCurationMapListByKeyword(SearchRequestDto searchRequestDto, Long lastCurationMapId, Pageable pageable);
    List<LocationSimpleQueryDto> findLocationListById(Long curationMapId); // 특정 큐레이션 지도의 장소 리스트 조회
}