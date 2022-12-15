package com.jinjjaseoul.domain.map.controller;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.map.dto.query.ThemeLocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.query.ThemeMapQueryDto;
import com.jinjjaseoul.domain.map.dto.request.LocationSimpleRequestDto;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.dto.request.SearchRequestDto;
import com.jinjjaseoul.domain.map.dto.request.ThemeMapRequestDto;
import com.jinjjaseoul.domain.map.dto.request.ThemeMapSimpleRequestDto;
import com.jinjjaseoul.domain.map.model.repository.theme_map.ThemeMapRepository;
import com.jinjjaseoul.domain.map.service.ThemeMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final RedisUtils redisUtils;

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

    /*
     * 임시 데이터 저장 (쿠키 vs 세션 vs 웹 스토리지 vs 캐시)
     * 1. 클라이언트에 저장 (쿠키)
     * 2. 서버에 저장 (세션)
     * 3. 세션 스토리지에 저장 : 보안이 중요한 임시 데이터 (ex. 일회성 로그인)
     * 4. 로컬 스토리지에 저장 : 보안이 중요한 영구 데이터 (ex. 자동 로그인)
     * 5. 캐시 메모리에 저장 (redis)
     */
    @Secured("ROLE_USER")
    @PostMapping("/save")
    public Response saveThemeMapInfoOnCache(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody ThemeMapSimpleRequestDto themeMapSimpleRequestDto) {
        // 쿠키 or 캐시에 저장 -> redis
        redisUtils.setCollection(
                String.valueOf(userPrincipal.getId()), // key
                themeMapSimpleRequestDto.getName(), // 0
                themeMapSimpleRequestDto.getIconId(), // 1
                themeMapSimpleRequestDto.getCategories(), // 2
                themeMapSimpleRequestDto.getKeywordStr() // 3
        );

        return Response.success(HttpStatus.CREATED);
    }

    @Secured("ROLE_USER")
    @PostMapping("/create")
    public Response createThemeMap(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody LocationSimpleRequestDto locationSimpleRequestDto) {
        List<Object> dataList = redisUtils.getDataListFromCollection(String.valueOf(userPrincipal.getId()), 0, 3);
        ThemeMapRequestDto themeMapRequestDto = ThemeMapRequestDto.builder()
                .name((String) dataList.get(0))
                .categories((List<Category>) dataList.get(2))
                .keywordStr((String) dataList.get(3))
                .locationId(locationSimpleRequestDto.getLocationId())
                .imageUrl(locationSimpleRequestDto.getImageUrl())
                .build();

        Long themeMapId = themeMapService.makeThemeMap(userPrincipal, (Long) dataList.get(1), themeMapRequestDto);
        redisUtils.deleteData(String.valueOf(userPrincipal.getId()));

        return Response.success(HttpStatus.CREATED, themeMapId);
    }

    @GetMapping("/{id}/locations")
    public Response getLocationList(@PathVariable("id") Long themeMapId) {
        List<ThemeLocationSimpleQueryDto> locationList = themeMapRepository.findLocationListById(themeMapId);
        return Response.success(HttpStatus.OK, locationList);
    }

    @Secured("ROLE_USER")
    @PostMapping("/update")
    public Response recommendThemeLocation(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam("id") Long themeMapId, @RequestParam Long locationId,
                                           @RequestParam(required = false) String imageUrl) {
        themeMapService.updateThemeLocation(userPrincipal, themeMapId, locationId, imageUrl);
        return Response.success(HttpStatus.CREATED, "테마 지도에 장소가 추가되었습니다.");
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/condition")
    public Response updateSearchCondition(@PathVariable("id") Long themeMapId, @RequestBody MapSearchRequestDto mapSearchRequestDto) {
        themeMapService.updateMapSearchTable(themeMapId, mapSearchRequestDto);
        return Response.success(HttpStatus.CREATED, "검색 조건을 업데이트 했습니다.");
    }

    @PostMapping("/search")
    public Response searchList(@RequestParam(required = false) String keyword, @RequestBody SearchRequestDto searchRequestDto,
                               @RequestParam(required = false) Long lastThemeMapId, @PageableDefault(size = 30) Pageable pageable) {
        Page<ThemeMapQueryDto> themeMapQueryDtoPage = themeMapRepository.searchThemeMapListByKeyword(keyword, searchRequestDto, lastThemeMapId, pageable);
        return Response.success(HttpStatus.CREATED, themeMapQueryDtoPage);
    }

    @DeleteMapping("/{themeMapId}/delete")
    public Response deleteThemeMap(@PathVariable Long themeMapId) {
        themeMapService.deleteThemeMap(themeMapId);
        return Response.success(HttpStatus.NO_CONTENT, "테마 지도를 성공적으로 삭제했습니다.");
    }

    @DeleteMapping("/{themeLocationId}/delete")
    public Response deleteThemeLocation(@PathVariable Long themeLocationId) {
        themeMapService.deleteThemeLocation(themeLocationId);
        return Response.success(HttpStatus.NO_CONTENT, "테마 장소를 성공적으로 삭제했습니다.");
    }
}