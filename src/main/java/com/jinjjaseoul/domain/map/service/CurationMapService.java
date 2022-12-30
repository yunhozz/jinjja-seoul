package com.jinjjaseoul.domain.map.service;

import com.jinjjaseoul.auth.model.UserPrincipal;
import com.jinjjaseoul.domain.icon.model.Icon;
import com.jinjjaseoul.domain.icon.model.IconRepository;
import com.jinjjaseoul.domain.location.model.entity.Location;
import com.jinjjaseoul.domain.location.model.repository.location.LocationRepository;
import com.jinjjaseoul.domain.map.dto.request.CurationMapRequestDto;
import com.jinjjaseoul.domain.map.dto.request.LocationSimpleRequestDto;
import com.jinjjaseoul.domain.map.dto.request.MapSearchRequestDto;
import com.jinjjaseoul.domain.map.model.entity.CurationLocation;
import com.jinjjaseoul.domain.map.model.entity.CurationMap;
import com.jinjjaseoul.domain.map.model.repository.CurationLocationRepository;
import com.jinjjaseoul.domain.map.model.repository.map.MapRepository;
import com.jinjjaseoul.domain.map.service.exception.CurationLocationNotFoundException;
import com.jinjjaseoul.domain.map.service.exception.CurationMapNameDuplicateException;
import com.jinjjaseoul.domain.map.service.exception.CurationMapNotFoundException;
import com.jinjjaseoul.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CurationMapService {

    private final IconRepository iconRepository;
    private final LocationRepository locationRepository;
    private final MapRepository<CurationMap> curationMapRepository;
    private final CurationLocationRepository curationLocationRepository;

    @Transactional
    public Long makeCurationMap(UserPrincipal userPrincipal, CurationMapRequestDto curationMapRequestDto) {
        validateCurationMapNameDuplicate(curationMapRequestDto); // 중복되는 이름 검증

        User user = userPrincipal.getUser();
        Icon icon = determineIcon(curationMapRequestDto.getIconId());
        CurationMap curationMap = createCurationMap(curationMapRequestDto, user, icon);

        curationMapRepository.save(curationMap);
        curationMap.updateRedirectUrl("localhost:8080/curation/" + curationMap.getId());

        return curationMap.getId();
    }

    // 하나의 큐레이션맵 당 여러 장소 추천 가능, 공개 여부에 따라 다른 유저도 추천 가능
    @Transactional
    public void addCurationLocation(UserPrincipal userPrincipal, Long curationMapId, LocationSimpleRequestDto locationSimpleRequestDto) {
        User user = userPrincipal.getUser();
        CurationMap curationMap = findCurationMap(curationMapId);
        Location location = locationRepository.getReferenceById(locationSimpleRequestDto.getLocationId());

        CurationLocation curationLocation = createCurationLocation(user, curationMap, location);
        curationLocationRepository.save(curationLocation);
    }

    // 검색용 테이블 업데이트 (운영자)
    @Transactional
    public void updateMapSearchTable(Long curationMapId, MapSearchRequestDto mapSearchRequestDto) {
        CurationMap curationMap = findCurationMap(curationMapId);
        curationMap.updateSearchCondition(
                mapSearchRequestDto.getPlace(),
                mapSearchRequestDto.getSomebody(),
                mapSearchRequestDto.getSomething(),
                mapSearchRequestDto.getCharacteristics(),
                mapSearchRequestDto.getFood(),
                mapSearchRequestDto.getBeverage(),
                mapSearchRequestDto.getCategory()
        );
    }

    @Transactional
    public void deleteCurationMap(Long curationMapId) {
        CurationMap curationMap = findCurationMap(curationMapId);
        curationMap.delete();
        updateTableByDeletingCurationMap(curationMapId);
    }

    @Transactional
    public void deleteCurationLocation(Long curationLocationId) {
        CurationLocation curationLocation = curationLocationRepository.findById(curationLocationId)
                .orElseThrow(CurationLocationNotFoundException::new);
        curationLocationRepository.delete(curationLocation);
    }

    @Transactional
    private void updateTableByDeletingCurationMap(Long curationMapId) {
        List<Long> curationLocationIds = curationLocationRepository.findIdsByCurationMapId(curationMapId);
        curationLocationRepository.deleteAllByIds(curationLocationIds); // 해당 큐레이션 지도의 큐레이션 장소 전체 삭제
    }

    private CurationMap createCurationMap(CurationMapRequestDto curationMapRequestDto, User user, Icon icon) {
        return CurationMap.builder()
                .user(user)
                .name(curationMapRequestDto.getName())
                .icon(icon)
                .isMakeTogether(curationMapRequestDto.getIsMakeTogether())
                .isProfileDisplay(curationMapRequestDto.getIsProfileDisplay())
                .isShared(curationMapRequestDto.getIsShared())
                .build();
    }

    private CurationLocation createCurationLocation(User user, CurationMap curationMap, Location location) {
        return CurationLocation.builder()
                .user(user)
                .curationMap(curationMap)
                .location(location)
                .build();
    }

    private CurationMap findCurationMap(Long curationMapId) {
        try {
            return curationMapRepository.findById(curationMapId)
                    .orElseThrow(CurationMapNotFoundException::new);

        } catch (ClassCastException e) {
            throw new CurationMapNotFoundException();
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

    private void validateCurationMapNameDuplicate(CurationMapRequestDto curationMapRequestDto) {
        if (curationMapRepository.existsByName(curationMapRequestDto.getName())) {
            throw new CurationMapNameDuplicateException();
        }
    }
}