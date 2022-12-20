package com.jinjjaseoul.domain.map.controller;

import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.map.dto.query.CurationMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.WholeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.request.SearchRequestDto;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maps/search")
@RequiredArgsConstructor
public class SearchMapController {

    private final MapRepository<?> mapRepository;

    @PostMapping
    public Response searchList(@RequestBody SearchRequestDto searchRequestDto, @RequestParam(required = false, defaultValue = "전체") String mapKind,
                               @RequestParam(required = false) Long lastMapId, @PageableDefault(size = 30) Pageable pageable) {
        switch (mapKind) {
            case "테마지도":
                Page<ThemeMapQueryDto> themeMapQueryDtoPage = mapRepository.searchThemeMapListByKeyword(searchRequestDto, lastMapId, pageable);
                return Response.success(HttpStatus.CREATED, themeMapQueryDtoPage);

            case "큐레이션지도":
                Page<CurationMapQueryDto> curationMapQueryDtoPage = mapRepository.searchCurationMapListByKeyword(searchRequestDto, lastMapId, pageable);
                return Response.success(HttpStatus.CREATED, curationMapQueryDtoPage);

            case "전체":
                Page<WholeMapQueryDto> wholeMapQueryDtoPage = mapRepository.searchWholeMapListByKeyword(searchRequestDto, lastMapId, pageable);
                return Response.success(HttpStatus.CREATED, wholeMapQueryDtoPage);

            default:
                return Response.failure(HttpStatus.BAD_REQUEST, "요청한 지도 종류를 확인해주세요.");
        }
    }
}