package com.jinjjaseoul.domain.map.controller;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.map.dto.query.CurationMapQueryDto;
import com.jinjjaseoul.domain.map.dto.query.LocationSimpleQueryDto;
import com.jinjjaseoul.domain.map.dto.request.CurationMapRequestDto;
import com.jinjjaseoul.domain.map.dto.request.LocationSimpleRequestDto;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import com.jinjjaseoul.domain.map.service.CurationMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/curations")
@RequiredArgsConstructor
public class CurationMapController {

    private final CurationMapService curationMapService;
    private final MapRepository<?> curationMapRepository;

    @GetMapping
    public Response getCurationMapList() {
        List<CurationMapQueryDto> curationMapQueryDtoList = curationMapRepository.findRandomList();
        return Response.success(HttpStatus.OK, curationMapQueryDtoList);
    }

    @GetMapping("/{id}")
    public Response getLocationList(@PathVariable("id") Long curationMapId) {
        List<LocationSimpleQueryDto> locationSimpleQueryDtoList = curationMapRepository.findLocationListByCurationMapId(curationMapId);
        return Response.success(HttpStatus.OK, locationSimpleQueryDtoList);
    }

    @Secured("ROLE_USER")
    @PostMapping("/create")
    public Response createCurationMap(@AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody CurationMapRequestDto curationMapRequestDto) {
        Long curationMapId = curationMapService.makeCurationMap(userPrincipal, curationMapRequestDto);
        return Response.success(HttpStatus.CREATED, curationMapId);
    }

    @Secured("ROLE_USER")
    @PostMapping("/{id}/update")
    public Response recommendCurationLocation(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable("id") Long curationMapId,
                                              @RequestBody LocationSimpleRequestDto locationSimpleRequestDto) {
        Long locationId = locationSimpleRequestDto.getLocationId();
        if (locationId == null) {
            return Response.failure(HttpStatus.BAD_REQUEST, "장소를 선택해주세요.");
        }

        curationMapService.addCurationLocation(userPrincipal, curationMapId, locationSimpleRequestDto);
        return Response.success(HttpStatus.CREATED, locationId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/condition")
    public Response updateSearchCondition(@PathVariable("id") Long curationMapId, @RequestBody MapSearchRequestDto mapSearchRequestDto) {
        curationMapService.updateMapSearchTable(curationMapId, mapSearchRequestDto);
        return Response.success(HttpStatus.CREATED, "검색 조건을 업데이트 했습니다.");
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PatchMapping("/{id}/delete")
    public Response deleteCurationMap(@PathVariable("id") Long curationMapId) {
        curationMapService.deleteCurationMap(curationMapId);
        return Response.success(HttpStatus.NO_CONTENT, "큐레이션 지도를 성공적으로 삭제했습니다.");
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @DeleteMapping("/{id}/delete")
    public Response deleteThemeLocation(@PathVariable("id") Long curationLocationId) {
        curationMapService.deleteCurationLocation(curationLocationId);
        return Response.success(HttpStatus.NO_CONTENT, "큐레이션 장소를 성공적으로 삭제했습니다.");
    }
}