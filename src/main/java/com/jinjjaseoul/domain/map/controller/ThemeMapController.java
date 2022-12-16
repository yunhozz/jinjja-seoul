package com.jinjjaseoul.domain.map.controller;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.map.dto.query.ThemeLocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.request.LocationSimpleRequestDto;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.dto.request.ThemeMapSimpleRequestDto;
import com.jinjjaseoul.domain.map.model.repository.theme_map.ThemeMapRepository;
import com.jinjjaseoul.domain.map.service.ThemeMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
public class ThemeMapController {

    private final ThemeMapService themeMapService;
    private final ThemeMapRepository themeMapRepository;

    @GetMapping("/recommend")
    public Response getRecommendList() {
        List<ThemeMapQueryDto> themeMapQueryDtoList = themeMapRepository.findRecommendList();
        return Response.success(HttpStatus.OK, themeMapQueryDtoList);
    }

    @GetMapping("/latest")
    public Response getLatestList() {
        List<ThemeMapQueryDto> themeMapQueryDtoList = themeMapRepository.findLatestList();
        return Response.success(HttpStatus.OK, themeMapQueryDtoList);
    }

    @GetMapping("/popular")
    public Response getPopularList() {
        List<ThemeMapQueryDto> themeMapQueryDtoList = themeMapRepository.findPopularList();
        return Response.success(HttpStatus.OK, themeMapQueryDtoList);
    }

    @GetMapping("/{id}/locations")
    public Response getLocationList(@PathVariable("id") Long themeMapId) {
        List<ThemeLocationSimpleQueryDto> locationList = themeMapRepository.findLocationListById(themeMapId);
        return Response.success(HttpStatus.OK, locationList);
    }

    @Secured("ROLE_USER")
    @PostMapping("/save")
    public Response saveThemeMapInfoOnCache(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody ThemeMapSimpleRequestDto themeMapSimpleRequestDto) {
        themeMapService.saveThemeMapInfoOnCache(userPrincipal, themeMapSimpleRequestDto);
        return Response.success(HttpStatus.CREATED);
    }

    @Secured("ROLE_USER")
    @PostMapping("/create")
    public Response createThemeMap(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody LocationSimpleRequestDto locationSimpleRequestDto) {
        Long themeMapId = themeMapService.makeThemeMap(userPrincipal.getId(), locationSimpleRequestDto);
        return Response.success(HttpStatus.CREATED, themeMapId);
    }

    @Secured("ROLE_USER")
    @PostMapping("/update")
    public Response recommendThemeLocation(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam("id") Long themeMapId,
                                           @RequestBody LocationSimpleRequestDto locationSimpleRequestDto) {
        if (locationSimpleRequestDto.getLocationId() == null) {
            return Response.failure(HttpStatus.BAD_REQUEST, "장소를 선택해주세요.");
        }

        themeMapService.updateThemeLocation(userPrincipal.getId(), themeMapId, locationSimpleRequestDto.getLocationId(), locationSimpleRequestDto.getImageUrl());
        return Response.success(HttpStatus.CREATED, "테마 지도에 장소가 추가되었습니다.");
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/condition")
    public Response updateSearchCondition(@PathVariable("id") Long themeMapId, @RequestBody MapSearchRequestDto mapSearchRequestDto) {
        themeMapService.updateMapSearchTable(themeMapId, mapSearchRequestDto);
        return Response.success(HttpStatus.CREATED, "검색 조건을 업데이트 했습니다.");
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @DeleteMapping("/{themeMapId}/delete")
    public Response deleteThemeMap(@PathVariable Long themeMapId) {
        themeMapService.deleteThemeMap(themeMapId);
        return Response.success(HttpStatus.NO_CONTENT, "테마 지도를 성공적으로 삭제했습니다.");
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @DeleteMapping("/{themeLocationId}/delete")
    public Response deleteThemeLocation(@PathVariable Long themeLocationId) {
        themeMapService.deleteThemeLocation(themeLocationId);
        return Response.success(HttpStatus.NO_CONTENT, "테마 장소를 성공적으로 삭제했습니다.");
    }
}