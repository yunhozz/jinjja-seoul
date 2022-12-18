package com.jinjjaseoul.domain.location.controller;

import com.jinjjaseoul.common.dto.Response;
import com.jinjjaseoul.domain.location.dto.query.LocationQueryDto;
import com.jinjjaseoul.domain.location.dto.request.LocationRequestDto;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final LocationRepository locationRepository;

    @GetMapping("/{id}")
    public Response getLocationInfo(@PathVariable("id") Long locationId) {
        LocationQueryDto locationQueryDto = locationRepository.findLocationInfoById(locationId);
        return Response.success(HttpStatus.OK, locationQueryDto);
    }

    @GetMapping("/search")
    public Response searchLocationsByKeyword(@RequestParam String keyword, @PageableDefault(size = 15) Pageable pageable) {
        Page<Location> locationPage = locationRepository.findByNameContaining(keyword, pageable);
        return Response.success(HttpStatus.OK, locationPage);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public Response createLocationInfo(@Valid @RequestBody LocationRequestDto locationRequestDto) {
        Long locationId = locationService.createLocation(locationRequestDto);
        return Response.success(HttpStatus.CREATED, locationId);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public Response deleteLocationInfo(@PathVariable("id") Long locationId) {
        locationService.deleteLocation(locationId);
        return Response.success(HttpStatus.NO_CONTENT, "해당 장소가 삭제되었습니다.");
    }
}