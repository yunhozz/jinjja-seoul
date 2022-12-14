package com.jinjjaseoul.domain.map.service;

import com.jinjjaseoul.common.converter.MapConverter;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.LocationRepository;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.dto.request.ThemeMapRequestDto;
import com.jinjjaseoul.domain.map.model.entity.ThemeLocation;
import com.jinjjaseoul.domain.map.model.entity.ThemeMap;
import com.jinjjaseoul.domain.map.model.repository.ThemeLocationRepository;
import com.jinjjaseoul.domain.map.model.repository.theme_map.ThemeMapRepository;
import com.jinjjaseoul.domain.map.service.exception.ThemeLocationNotFoundException;
import com.jinjjaseoul.domain.map.service.exception.ThemeMapNotFoundException;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeMapService {

    private final UserRepository userRepository;
    private final IconRepository iconRepository;
    private final LocationRepository locationRepository;
    private final ThemeMapRepository themeMapRepository;
    private final ThemeLocationRepository themeLocationRepository;

    @Transactional
    public Long makeThemeMap(Long userId, Long iconId, ThemeMapRequestDto themeMapRequestDto) {
        // 장소를 등록하지 않은 경우
        if (themeMapRequestDto.getLocationId() == null) {
            log.debug("장소를 등록하지 않았습니다.");
            return null;
        }

        User user = userRepository.getReferenceById(userId);
        Icon icon = determineIcon(iconId);
        ThemeMap themeMap = MapConverter.convertToThemeMapEntity(themeMapRequestDto, user, icon);
        Location location = locationRepository.getReferenceById(themeMapRequestDto.getLocationId());

        ThemeLocation themeLocation = createThemeLocation(user, themeMap, location, themeMapRequestDto.getImageUrl());
        themeLocationRepository.save(themeLocation);

        return themeMapRepository.save(themeMap).getId();
    }

    // 하나의 테마맵 당 하나의 장소만 추천 가능
    @Transactional
    public void updateThemeLocation(Long userId, Long themeMapId, Long locationId, String imageUrl) {
        User user = userRepository.getReferenceById(userId);
        ThemeMap themeMap = findThemeMap(themeMapId);
        Location location = locationRepository.getReferenceById(locationId);

        if (themeMap.isMadeByUser(user)) {
            ThemeLocation themeLocation = themeLocationRepository.findByUser(user)
                    .orElseThrow(ThemeLocationNotFoundException::new);
            themeLocation.update(location, imageUrl);

        } else {
            ThemeLocation themeLocation = createThemeLocation(user, themeMap, location, imageUrl);
            themeLocationRepository.save(themeLocation);
        }
    }

    // 검색용 테이블 업데이트 (운영자)
    @Transactional
    public void updateMapSearchTable(Long themeMapId, MapSearchRequestDto mapSearchRequestDto) {
        ThemeMap themeMap = findThemeMap(themeMapId);
        themeMap.updateSearchCondition(
                mapSearchRequestDto.getPlace(),
                mapSearchRequestDto.getSomebody(),
                mapSearchRequestDto.getSomething(),
                mapSearchRequestDto.getCharacteristics(),
                mapSearchRequestDto.getFood(),
                mapSearchRequestDto.getBeverage()
        );
    }

    @Transactional
    public void deleteThemeMap(Long themeMapId) {
        ThemeMap themeMap = findThemeMap(themeMapId);
        List<Long> themeLocationIds = themeLocationRepository.findIdsByThemeMapId(themeMap.getId());

        themeLocationRepository.deleteAllByIds(themeLocationIds);
        themeMapRepository.delete(themeMap);
    }

    @Transactional
    public void deleteThemeLocation(Long themeLocationId) {
        ThemeLocation themeLocation = themeLocationRepository.findById(themeLocationId)
                .orElseThrow(ThemeLocationNotFoundException::new);
        themeLocationRepository.delete(themeLocation);
    }

    private ThemeLocation createThemeLocation(User user, ThemeMap themeMap, Location location, String imageUrl) {
        return ThemeLocation.builder()
                .user(user)
                .themeMap(themeMap)
                .location(location)
                .imageUrl(imageUrl)
                .build();
    }

    private ThemeMap findThemeMap(Long themeMapId) {
        return themeMapRepository.findById(themeMapId)
                .orElseThrow(ThemeMapNotFoundException::new);
    }

    private Icon determineIcon(Long iconId) {
        Icon icon;
        if (iconId != null) {
            icon = iconRepository.getReferenceById(iconId);

        } else {
            Random random = new Random(System.currentTimeMillis());
            return iconRepository.getReferenceById((long) random.nextInt(8) + 1);
        }

        return icon;
    }
}