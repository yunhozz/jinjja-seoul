package com.jinjjaseoul.domain.map.service;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.common.enums.Category;
import com.jinjjaseoul.common.utils.RedisUtils;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.map.dto.request.LocationSimpleRequestDto;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.dto.request.ThemeMapRequestDto;
import com.jinjjaseoul.domain.map.dto.request.ThemeMapSimpleRequestDto;
import com.jinjjaseoul.domain.map.model.entity.ThemeLocation;
import com.jinjjaseoul.domain.map.model.entity.ThemeMap;
import com.jinjjaseoul.domain.map.model.repository.ThemeLocationRepository;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import com.jinjjaseoul.domain.map.service.exception.ThemeLocationNotFoundException;
import com.jinjjaseoul.domain.map.service.exception.ThemeMapNameDuplicateException;
import com.jinjjaseoul.domain.map.service.exception.ThemeMapNotFoundException;
import com.jinjjaseoul.domain.user.model.User;
import com.jinjjaseoul.domain.user.model.repository.UserRepository;
import com.jinjjaseoul.domain.user.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ThemeMapService {

    private final UserRepository userRepository;
    private final IconRepository iconRepository;
    private final LocationRepository locationRepository;
    private final MapRepository<ThemeMap> themeMapRepository;
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
    public Long makeThemeMap(Long userId, LocationSimpleRequestDto locationSimpleRequestDto) {
        List<Object> dataList = redisUtils.getDataListFromCollection(String.valueOf(userId), 0, 3);
        ThemeMapRequestDto themeMapRequestDto = createThemeMapDto(locationSimpleRequestDto, dataList);

        if (themeMapRequestDto.getLocationId() == null) return null; // 장소 등록 여부 검증

        User user = userRepository.getReferenceById(userId);
        Icon icon = determineIcon((Long) dataList.get(1));
        ThemeMap themeMap = createThemeMap(themeMapRequestDto, user, icon);
        Location location = locationRepository.getReferenceById(themeMapRequestDto.getLocationId());

        ThemeLocation themeLocation = createThemeLocation(user, themeMap, location);
        themeLocationRepository.save(themeLocation);
        themeLocation.addNumOfUserRecommend(); // 장소 추천수 +1
        redisUtils.deleteData(String.valueOf(userId));

        return themeMapRepository.save(themeMap).getId();
    }

    // 하나의 테마맵 당 하나의 장소만 추천 가능
    @Transactional
    public void updateThemeLocation(Long userId, Long themeMapId, LocationSimpleRequestDto locationSimpleRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        ThemeMap themeMap = findThemeMap(themeMapId);
        Location location = locationRepository.getReferenceById(locationSimpleRequestDto.getLocationId());

        if (themeMap.isMadeByUser(user)) {
            ThemeLocation themeLocation = themeLocationRepository.findByUserAndThemeMap(user, themeMap)
                    .orElseThrow(ThemeLocationNotFoundException::new);
            themeLocation.updateLocation(location);

        } else {
            themeLocationRepository.findByUserAndThemeMap(user, themeMap).ifPresentOrElse(themeLocation -> themeLocation.updateLocation(location), () -> {
                ThemeLocation themeLocation = createThemeLocation(user, themeMap, location);
                themeLocationRepository.save(themeLocation);
                themeLocation.addNumOfUserRecommend(); // 장소 추천수 +1
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
        ThemeMap themeMap = findThemeMap(themeMapId);
        themeMap.delete();
        updateTablesByDeletingThemeMap(themeMapId);
    }

    @Transactional
    public void deleteThemeLocation(Long themeLocationId) {
        ThemeLocation themeLocation = themeLocationRepository.findWithUserById(themeLocationId)
                .orElseThrow(ThemeLocationNotFoundException::new);
        themeLocation.subtractNumOfUserRecommend(); // 장소 추천수 -1
        themeLocationRepository.delete(themeLocation);
    }

    @Transactional
    protected void updateTablesByDeletingThemeMap(Long themeMapId) {
        List<Long> themeLocationIds = themeLocationRepository.findIdsByThemeMapId(themeMapId);
        List<Long> userIds = userRepository.findIdsByThemeLocationIds(themeLocationIds);

        themeLocationRepository.deleteAllByIds(themeLocationIds); // 해당 테마 지도의 테마 장소 전체 삭제
        userRepository.subtractNumOfRecommendInIds(userIds); // 해당 테마 지도의 유저들의 추천수 -1
    }

    private ThemeMap createThemeMap(ThemeMapRequestDto themeMapRequestDto, User user, Icon icon) {
        List<String> keywordList = new ArrayList<>() {{
            String[] keyword = themeMapRequestDto.getKeywordStr().split(",");
            this.addAll(Arrays.asList(keyword));
        }};

        return ThemeMap.builder()
                .user(user)
                .name(themeMapRequestDto.getName())
                .icon(icon)
                .categories(themeMapRequestDto.getCategories())
                .keywordList(keywordList)
                .build();
    }

    private ThemeLocation createThemeLocation(User user, ThemeMap themeMap, Location location) {
        return ThemeLocation.builder()
                .user(user)
                .themeMap(themeMap)
                .location(location)
                .build();
    }

    private ThemeMapRequestDto createThemeMapDto(LocationSimpleRequestDto locationSimpleRequestDto, List<Object> dataList) {
        return ThemeMapRequestDto.builder()
                .name((String) dataList.get(0))
                .categories((List<Category>) dataList.get(2))
                .keywordStr((String) dataList.get(3))
                .locationId(locationSimpleRequestDto.getLocationId())
                .build();
    }

    private ThemeMap findThemeMap(Long themeMapId) {
        try {
            return themeMapRepository.findById(themeMapId)
                    .orElseThrow(ThemeMapNotFoundException::new);

        } catch (ClassCastException e) {
            throw new ThemeMapNotFoundException();
        }
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