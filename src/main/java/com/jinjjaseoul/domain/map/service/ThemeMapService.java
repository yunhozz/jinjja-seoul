package com.jinjjaseoul.domain.map.service;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.converter.MapConverter;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.LocationRepository;
import com.jinjjaseoul.domain.map.dto.request.LocationSimpleRequestDto;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.dto.request.ThemeMapRequestDto;
import com.jinjjaseoul.domain.map.dto.request.ThemeMapSimpleRequestDto;
import com.jinjjaseoul.domain.map.model.entity.ThemeLocation;
import com.jinjjaseoul.domain.map.model.entity.ThemeMap;
import com.jinjjaseoul.domain.map.model.repository.ThemeLocationRepository;
import com.jinjjaseoul.domain.map.model.repository.theme_map.ThemeMapRepository;
import com.jinjjaseoul.domain.map.service.exception.ThemeLocationNotFoundException;
import com.jinjjaseoul.domain.map.service.exception.ThemeMapNameDuplicateException;
import com.jinjjaseoul.domain.map.service.exception.ThemeMapNotFoundException;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ThemeMapService {

    private final UserRepository userRepository;
    private final IconRepository iconRepository;
    private final LocationRepository locationRepository;
    private final ThemeMapRepository themeMapRepository;
    private final ThemeLocationRepository themeLocationRepository;
    private final RedisUtils redisUtils;

    /*
     * 임시 데이터 저장 (쿠키 vs 세션 vs 웹 스토리지 vs 캐시)
     * 1. 클라이언트에 저장 (쿠키)
     * 2. 서버에 저장 (세션)
     * 3. 세션 스토리지에 저장 : 보안이 중요한 임시 데이터 (ex. 일회성 로그인)
     * 4. 로컬 스토리지에 저장 : 보안이 중요한 영구 데이터 (ex. 자동 로그인)
     * 5. 캐시 메모리에 저장 (redis)
     */
    public void saveThemeMapInfoOnCache(UserPrincipal userPrincipal, ThemeMapSimpleRequestDto themeMapSimpleRequestDto) {
        validateThemeMapNameDuplicate(themeMapSimpleRequestDto.getName()); // 중복되는 이름 검증
        redisUtils.setCollection(
                String.valueOf(userPrincipal.getId()), // key
                themeMapSimpleRequestDto.getName(), // 0
                themeMapSimpleRequestDto.getIconId(), // 1
                themeMapSimpleRequestDto.getCategories(), // 2
                themeMapSimpleRequestDto.getKeywordStr() // 3
        );
    }

    @Transactional
    public Long makeThemeMap(UserPrincipal userPrincipal, LocationSimpleRequestDto locationSimpleRequestDto) {
        List<Object> dataList = redisUtils.getDataListFromCollection(String.valueOf(userPrincipal.getId()), 0, 3);
        ThemeMapRequestDto themeMapRequestDto = ThemeMapRequestDto.builder()
                .name((String) dataList.get(0))
                .categories((List<Category>) dataList.get(2))
                .keywordStr((String) dataList.get(3))
                .locationId(locationSimpleRequestDto.getLocationId())
                .imageUrl(locationSimpleRequestDto.getImageUrl())
                .build();

        if (themeMapRequestDto.getLocationId() == null) return null; // 장소 등록 여부 검증

        User user = userPrincipal.getUser();
        Icon icon = determineIcon(locationSimpleRequestDto.getLocationId());
        ThemeMap themeMap = MapConverter.convertToThemeMapEntity(themeMapRequestDto, user, icon);
        Location location = locationRepository.getReferenceById(themeMapRequestDto.getLocationId());

        ThemeLocation themeLocation = createThemeLocation(user, themeMap, location, themeMapRequestDto.getImageUrl());
        themeLocationRepository.save(themeLocation);
        user.addNumOfRecommend(); // 장소 추천수 +1
        redisUtils.deleteData(String.valueOf(userPrincipal.getId()));

        return themeMapRepository.save(themeMap).getId();
    }

    // 하나의 테마맵 당 하나의 장소만 추천 가능
    @Transactional
    public void updateThemeLocation(UserPrincipal userPrincipal, Long themeMapId, Long locationId, String imageUrl) {
        User user = userPrincipal.getUser();
        ThemeMap themeMap = findThemeMap(themeMapId);
        Location location = locationRepository.getReferenceById(locationId);

        if (themeMap.isMadeByUser(user)) {
            ThemeLocation themeLocation = themeLocationRepository.findByUserAndThemeMap(user, themeMap)
                    .orElseThrow(ThemeLocationNotFoundException::new);
            themeLocation.update(location, imageUrl);

        } else {
            themeLocationRepository.findByUserAndThemeMap(user, themeMap).ifPresentOrElse(themeLocation -> themeLocation.update(location, imageUrl), () -> {
                ThemeLocation themeLocation = createThemeLocation(user, themeMap, location, imageUrl);
                themeLocationRepository.save(themeLocation);
                user.addNumOfRecommend(); // 장소 추천수 +1
            });
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
        ThemeMap themeMap = themeMapRepository.findWithUserById(themeMapId)
                .orElseThrow(ThemeMapNotFoundException::new);
        List<Long> themeLocationIds = themeLocationRepository.findIdsByThemeMapId(themeMap.getId());

        themeMap.subtractNumOfUserRecommend(); // 장소 추천수 -1
        themeLocationRepository.deleteAllByIds(themeLocationIds);
        themeMapRepository.delete(themeMap);
    }

    @Transactional
    public void deleteThemeLocation(Long themeLocationId) {
        ThemeLocation themeLocation = themeLocationRepository.findWithUserById(themeLocationId)
                .orElseThrow(ThemeLocationNotFoundException::new);
        themeLocation.subtractNumOfUserRecommend(); // 장소 추천수 -1
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

    private void validateThemeMapNameDuplicate(String name) {
        if (themeMapRepository.existsByName(name)) {
            throw new ThemeMapNameDuplicateException();
        }
    }
}